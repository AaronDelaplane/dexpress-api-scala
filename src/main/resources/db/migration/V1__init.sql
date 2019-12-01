create extension if not exists "uuid-ossp";

create table tradable_assets(
    id               uuid    not null primary key,
    refresh_id       text    not null,
    steam_id         text    not null,
    appid            int     not null,
    assetid          text    not null,  
    classid          text    not null,
    instanceid       text    not null, 
    market_hash_name text    not null,
    icon_url         text    not null,
    asset_type       text    not null,
    exterior         text,
    rarity           text    not null,
    link_id          text,
    sticker_info     text    not null,
    trading          boolean not null
);
    