# Dexpress API Scala

## How To Run On Your Local Machine

The following steps create a local PostgreSQL database and an API server that accepts requests on `http://localhost:10000`.

Please see the **API Endpoints** section below this section for endpoint definitions.

1. **Install [Docker Desktop](https://www.docker.com/products/docker-desktop)**

2. **Create a new directory:**
```
mkdir temp
```

3) **Create a new Docker Compose YAML file:**
```
cd temp
touch docker-compose.yml
```

4) **Paste the following service definitions in the `docker-compose.yml` file:**
```yaml
version: '3.7'
services:
  api:
    image: aarondelaplane/dexpress-api-scala:latest
    ports:
      - 10000:10000
    depends_on:
      - postgres
  postgres:
    image: postgres:12.1
    ports:
      - 5432:5432
    environment:
      POSTGRES_PASSWORD: password
      POSTGRES_USER:     postgres
      POSTGRES_DB:       dexpress
```

5) **Create and start the services defined in the docker-compose.yml file**
```
cd temp
docker-compose up 
```
---

## How To Rebuild API And Datastore From Scratch
```
> ctrl-c // to stop running containers
> docker-compose down
> docker-compose up --force-recreate
```

---

## How To Build And Push A New Docker Image For API
```
> cd <project root>
> sbt
sbt> clean
sbt> assembly
sbt> exit
> docker build -t aarondelaplane/dexpress-api-scala:latest .
// new image is now available locally
> docker push aarondelaplane/dexpress-api-scala:latest
// new image is now available remotely
```

---

## API Endpoints
Endpoints may _only_ return `200`, `204`, or one of the error responses listed below.

Note that the term _resource_ used below is meant in the general sense.  For example, a PostgreSQL database, table, or row is a resource as are the several third-party API endpoints that this API calls under the hood.

#### Schema / Response / Status 400 (Bad Request)
```json
"bad request: <helpful error message>"
```
#### Schema / Response / Status 401 (Unauthorized)
```json
"<resource name> authentication error: <helpful error message>"
```
#### Schema / Response / Status 404 (Not Found)
```json
"<resource name> not found error: <helpful error message>"
```
#### Schema / Response / Status 409 (Conflict)
```json
"<resource name> invalid duplicate state error: <helpful error message>"
```
```json
"<resource name> invalid state change attempt error: <helpful error message>"
```
#### Schema / Response / Status 500 (Internal Server Error)
```json
"data transformation error: <helpful error message>"
```
```json
"<resource name> generic error: <helpful error message>"
```
```json
"<resource name> transaction error: <helpful error message>"
``` 

---

### Fetch Assets
```
GET /assets
```
#### Parameter / Query / Required:
```
trading: boolean
offset:  integer range[0 - 1000]
limit:   integer range[0 - 1000]
```
#### Parameter / Query / Optional:  

- `filter` and `filternot` may not be used together in the same request
```
filter:    id_user uuid
filternot: id_user uuid
```
#### Examples:
```
Return the first 100 trading|non-trading assets:

GET /assets?trading=<boolean>&offset=0&limit=100
```
```
Return all trading|non-trading assets for id_user:

GET /assets?trading=<boolean>?filter=<uuid>
```
```
Return all trading|non-trading assets except those for id_user: 

GET /assets?trading=<true>&filternot=<uuid>
```  
```
Return x number of trading|non-trading assets:

GET /assets?trading=<boolean>&limit=<integer>
```
```
Return x number of trading|non-trading assets for id_user:

GET /assets?trading=<boolean>&filter=<uuid>&limit=<integer>
```
```
Return all trading|non-trading assets in successive series of 10 per call:

GET /assets?trading=<boolean>&offset=0&limit=10
GET /assets?trading=<boolean>&offset=10&limit=10
GET /assets?trading=<boolean>&offset=20&limit=10
```
```
Return all trading|non-trading assets for id_user in successive series of 10 per call:

GET /assets?trading=<boolean>&offset=0&limit=10
GET /assets?trading=<boolean>&offset=10&limit=10
GET /assets?trading=<boolean>&offset=20&limit=10
```
#### Schema / Response / Status 200 (Ok):
```
[
  {
    id_asset:                             uuid
    id_user:                              uuid 
    id_asset_steam:                       string  
    id_user_steam:                        string
    id_class:                             string
    id_instance:                          string
    id_link:                              string | null    
    id_app:                               integer
    is_trading:                           boolean      
    type_asset:                           string
    float_value:                          float | null   <- always float for trading assets
    amount:                               string
    market_hash_name:                     string
    icon_url:                             ?
    sticker_urls:                         [] url
    tag_exterior_category:                string | null
    tag_exterior_internal_name:           string | null
    tag_exterior_localized_category_name: string | null
    tag_exterior_localized_tag_name:      string | null
    tag_rarity_category:                  string | null
    tag_rarity_internal_name:             string | null
    tag_rarity_localized_category_name:   string | null
    tag_rarity_localized_tag_name:        string | null
    tag_rarity_color:                     string | null
    tag_type_category:                    string | null
    tag_type_internal_name:               string | null
    tag_type_localized_category_name:     string | null
    tag_type_localized_tag_name:          string | null
    tag_weapon_category:                  string | null
    tag_weapon_internal_name:             string | null
    tag_weapon_localized_category_name:   string | null
    tag_weapon_localized_tag_name:        string | null
    tag_quality_category:                 string | null
    tag_quality_internal_name:            string | null
    tag_quality_localized_category_name:  string | null
    tag_quality_localized_tag_name:       string | null
    tag_quality_color:                    string | null
  },
  {},
  {}
]
```
#### Notes:
- Ordering of returned assets is currently based off of property `id_asset`. This is a temporary solution to enforce consistent results. In the future, ordering will be based off of more sophisticated search parameters.
- A user may have a maximum of 1000 tradable assets. This limitation is set by Steam.
- Calls to fetch assets for a specific user result in the following workflow:
```
if (user's inventory does not exist in Dexpress' data store)
    => read user's inventory from Steam's data store
    => write user's inventory to Dexpress' data store
    => return user's inventory from Dexpress' data store 
else if (user's inventory exists in Dexpress' data store)
    if (refresh time period not expired)
        => return user's inventory from Dexpress' data store
    else if (refresh time period expired)
        => read user's inventory from Steam's data store
        => write user's inventory to Dexpress' data store maintaining state of still-valid assets
        => return user's inventory from Dexpress' data store        
```

---

### Check If `id_user_steam` Maps To Existing User
```
GET /user/exists
```
#### Parameter / Query / Required:
```
idusersteam: string
```
#### Examples:
```
GET /user/exists?idusersteam=<id_user_steam>
```
#### Schema / Response / Status 200 (Ok):
```
<boolean>
```

---

### Save New User
```
POST /user
```
#### Schema / Request:
```
{
  id_user_steam:   string   
  user_name_first: string  
}
```
#### Schema / Response / Status 200 (Ok):
```
{
  id_user_steam:   string
  user_name_first: string
  id_user:         uuid
}
```

---

### Fetch Existing User
```
GET /user
```
#### Parameter / Query / Required:
```
idusersteam: string
```
#### Example:
```
GET /user?idusersteam=<id_user_steam>
```
#### Schema / Response / Status 200 (Ok):
```
{
  id_user_steam:   string
  user_name_first: string
  id_user:         uuid
}
```

---

### Update Asset
```
PATCH /asset
```
#### Parameter / Query / Required:
```
idasset: uuid
trading: boolean
```
#### Example:
```
PATCH /asset?idasset=<uuid>&trading=<boolean>
```