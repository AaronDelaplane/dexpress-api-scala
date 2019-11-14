## WIP...
### Dexpress Inventory Service


#### PostgreSQL Data Store
```
schema/table: 
inventory.refresh_events

summary:
- tracks inventory refresh events for all users

columns:
- id:            INT PRIMARY KEY NON NULL
- user_id:       TEXT NOT NULL  
- start_time:    TIMESTAMPTZ NOT NULL
- end_time:      TIMESTAMPTZ NOT NULL
- completion_ok: BOOLEAN NOT NULL                      
```
```
schema/table: 
inventory.nonvalidated_items

summary:
- tracks inventory state as defined by https://steamcommunity.com/inventory/<STEAM_ID>
- each row linked to a single user and a single refresh event
- non-validated data

columns:
- id:               INT NOT NULL PRIMARY KEY 
- appid:            INT,
- assetid:          TEXT,
- classid:          TEXT,
- instanceid:       TEXT,
- tradable:         INT,
- market_hash_name: TEXT,
- icon_url:         TEXT,
- item_data:        TEXT,
- type:             TEXT,
- rarity:           TEXT,
- exterior:         TEXT,
- sticker_info:     TEXT
```
```
schema/table:
inventory.validated_items

summary:
- sources data from inventory.items table
- data deemed valid for sale on the exchange

columns:
- id:               INT  NOT NULL PRIMARY KEY
- appid:            INT  NOT NULL
- assetid:          TEXT NOT NULL
- classid:          TEXT NOT NULL
- instanceid:       TEXT NOT NULL
- tradable:         INT  NOT NULL
- market_hash_name: TEXT NOT NULL
- icon_url:         TEXT NOT NULL
- item_data:        TEXT NOT NULL
- type:             TEXT NOT NULL
- rarity:           TEXT NOT NULL
- exterior:         TEXT NOT NULL
- sticker_info:     TEXT NOT NULL
```
```
table/schema: inventory.validation_events
```

#### Endpoints
```
summary:
- fetch a user's inventory 

action/uri:
GET <HOST>/v1/inventory/exchange/user/<USER_ID>
    
Response:
[
    {
        "appid":            Positve Number,
        "assetid":          NonEmptyString,
        "classid":          NonEmptyString,
        "instanceid":       NonEmptyString,
        "tradable":         0 || 1
        "market_hash_name": NonEmptyString,
        "icon_url":         NonEmptyString,
        "item_data":        NonEmptyString,
        "`type`":           NonEmptyString,
        "rarity":           NonEmptyString,
        "exterior":         NonEmptyString,
        "sticker_info":     NonEmptyString
  }
]

  
```


```
GET <host>/v1/inventory/user/refresh/<steamId>

```

`GET `
