data_a tags
- none are required
- but a look-up for each tag must be performed for all assets
- path in response is `response.descriptions[x].tags` <- array of objects
- schemas below apply to all tags of given type in darius' inventory

exterior
- never has a color property for darius
```json
{
    "category": "Exterior",
    "internal_name": "WearCategory2",
    "localized_category_name": "Exterior",
    "localized_tag_name": "Field-Tested"
}
```

rarity
- always has a color property for darius
```json
{
    "category": "Rarity",
    "internal_name": "Rarity_Common",
    "localized_category_name": "Quality",
    "localized_tag_name": "Base Grade",
    "color": "b0c3d9"
}
```

type
```json
{
    "category": "Type",
    "internal_name": "CSGO_Type_Spray",
    "localized_category_name": "Type",
    "localized_tag_name": "Graffiti"
}
```

weapon
```json
{
    "category": "Weapon",
    "internal_name": "weapon_g3sg1",
    "localized_category_name": "Weapon",
    "localized_tag_name": "G3SG1"
}
```
quality
```json
[
  {
      "category": "Quality",
      "internal_name": "tournament",
      "localized_category_name": "Category",
      "localized_tag_name": "Souvenir"
  },
  {
      "category": "Quality",
      "internal_name": "tournament",
      "localized_category_name": "Category",
      "localized_tag_name": "Souvenir",
      "color": "FFD700"
  }
]
```
                
                
                
                
                
                
                