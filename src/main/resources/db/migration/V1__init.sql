create extension if not exists "uuid-ossp";

--create schema steam;
--create schema dexpress;

create table users (
    name text not null
);

create table assets_steam_nonvalidated (
    id                  UUID NOT NULL PRIMARY KEY,
    refresh_event_id    TEXT NOT NULL,
    users_steam_id_hash TEXT NOT NULL,
    appid               INT,
    assetid             TEXT,  
    classid             TEXT,
    instanceid          TEXT, 
    tradable            INT,
    market_hash_name    TEXT,
    icon_url            TEXT,
    item_data           TEXT,
    type                TEXT,
    rarity              TEXT,
    exterior            TEXT,
    sticker_info        TEXT
);

--create table refresh_event_steam_nonvalidated (
--);

--create table steam.assets_validated
--);
--
--create table inventory_dexpress_stateless (
--);
--
--create table inventory_dexpress_stateful (
--);    
    