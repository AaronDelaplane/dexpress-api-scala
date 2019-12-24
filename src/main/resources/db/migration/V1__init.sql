-- noinspection SqlNoDataSourceInspectionForFile

create extension if not exists "uuid-ossp";

set time zone 'UTC';

create table assets(
    -- service-defined properties
    dexpress_asset_id uuid     not null primary key, -- dexpress-defined property
    refresh_id        uuid     not null,             -- dexpress-defined property
    trading           boolean  not null,
    -- user-defined properties
    steam_id          text     not null,             -- todo encrypt <- not a priority while developing locally
    -- taken from `https://api.csgofloat.com/` response. call is only made when an asset.trading is updated to `true`
    floatvalue        float, 
    -- all properties below line taken from `https://steamcommunity.com/inventory/` response ---------------------------
    -- asset/description-defined properties 
    classid           text     not null,             -- name format matches json response
    instanceid        text     not null,             -- name format matches json response
    appid             int      not null,             -- name format matches json response
    -- asset-defined properties    
    assetid           text     not null,             -- name format matches json response  
    amount            text     not null,             -- name format matches json response
    -- description-defined properties
    market_hash_name  text     not null,             -- name format matches json response
    icon_url          text     not null,             -- name format matches json response
    tradable          int      not null,             -- name format matches json response
    type              text     not null,             -- name format matches json response
    link_id           text,                          -- maybe present
    sticker_urls      text[],                        -- maybe present. values are URL strings
    -- tags ------------------------------------------------------------------------------------------------------------
    -- note: a given item will never have multiples of a tag, though all tags may or may not be present
    -- note: tags are not partially present.  all values are present or all values are not present, save `tag_quality_color` 
    -- tag exterior
    tag_exterior_category                text,
    tag_exterior_internal_name           text,
    tag_exterior_localized_category_name text,
    tag_exterior_localized_tag_name      text,
    -- tag_exterior_color                text, -- never present
    -- tag rarity    
    tag_rarity_category                  text,
    tag_rarity_internal_name             text,
    tag_rarity_localized_category_name   text,
    tag_rarity_localized_tag_name        text,
    tag_rarity_color                     text, -- always present given tag_rarity exists
    -- tag type   
    tag_type_category                    text,
    tag_type_internal_name               text,
    tag_type_localized_category_name     text,
    tag_type_localized_tag_name          text,
    -- tag_type_color                    text, -- never present
    -- tag weapon
    tag_weapon_category                  text,
    tag_weapon_internal_name             text,
    tag_weapon_localized_category_name   text,
    tag_weapon_localized_tag_name        text,
    -- tag_weapon_color                  text, -- never present
    -- taq quality
    tag_quality_category                 text,
    tag_quality_internal_name            text,
    tag_quality_localized_category_name  text,
    tag_quality_localized_tag_name       text,
    tag_quality_color                    text  -- maybe present
);

comment on table assets is 'required data for item to be listed in user''s inventory';

create table events_refresh_assets(
    refresh_id uuid not null primary key,
    steam_id   text not null,
    time       int  not null                      
);

comment on table events_refresh_assets is '';
    