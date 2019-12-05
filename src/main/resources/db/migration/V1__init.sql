create extension if not exists "uuid-ossp";

set time zone 'UTC';

create table assets_data_a(
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
    sticker_info     text    not null
    --trading          boolean not null
);

comment on table assets_data_a is 'required data for item to be listed in user''s inventory';

create table assets_data_b(
    id    uuid    not null primary key,
    float decimal not null
);

comment on table assets_data_b is 'required data for item to be marked as trading';

create table assets_trading(
    id uuid not null primary key
);

comment on table assets_trading is 'a set of ids representing all assets currently trading';

create table events_refresh_assets_data_a(
    id   uuid        not null primary key,
    time timestamptz not null                      
)
    