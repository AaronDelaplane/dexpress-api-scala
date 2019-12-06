create extension if not exists "uuid-ossp";

set time zone 'UTC';

create table assets_data_a(
    -- service-defined properties
    dexpress_asset_id uuid     not null primary key, -- dexpress-defined property
    refresh_id        uuid     not null,             -- dexpress-defined property
    -- user-defined properties
    steam_id          text     not null,             -- todo encrypt <- not a priority while developing locally
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

comment on table assets_data_a is 'required data for item to be listed in user''s inventory';

create table assets_data_b(
    dexpress_asset_id uuid    not null primary key,
    float             decimal not null
);

comment on table assets_data_b is 'required data for item to be marked as trading';

create table assets_trading(
    dexpress_asset_id uuid not null primary key
);

comment on table assets_trading is 'a set of ids representing all assets currently trading';

create table events_refresh_assets_data_a(
    id   uuid        not null primary key,
    time timestamptz not null                      
)
    