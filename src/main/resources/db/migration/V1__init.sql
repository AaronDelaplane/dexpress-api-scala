create extension if not exists "uuid-ossp";

create table assets(
    id               UUID NOT NULL PRIMARY KEY,
    refresh_id       TEXT NOT NULL,
    steam_id         TEXT NOT NULL,
    appid            INT  NOT NULL,
    assetid          TEXT NOT NULL,  
    classid          TEXT NOT NULL,
    instanceid       TEXT NOT NULL, 
    tradable         INT  NOT NULL,
    market_hash_name TEXT NOT NULL,
    icon_url         TEXT NOT NULL,
    asset_type       TEXT NOT NULL,
    exterior         TEXT,
    rarity           TEXT NOT NULL,
    item_data        TEXT NOT NULL,
    sticker_info     TEXT NOT NULL
);   
    