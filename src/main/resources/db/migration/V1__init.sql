create extension if not exists "uuid-ossp";

set time zone 'UTC';

create table assets(
    id               uuid    not null primary key,
    refresh_id       uuid    not null,
    steam_id         text    not null,
    appid            int     not null,
    assetid          text    not null,  
    classid          text    not null,
    instanceid       text    not null, 
    market_hash_name text    not null,
    icon_url         text    not null,
    asset_type       text    not null,
    exterior         text,   -- must be optional
    rarity           text    not null,
    link_id          text,   -- must be optional
    sticker_info     text    not null,
    trading          boolean not null
);

create table refresh_events(
    id   uuid not null primary key,
    time timestamptz not null                      
)
    