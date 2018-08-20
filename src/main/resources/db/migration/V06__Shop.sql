
create table shop(
    id serial not null,

    name varchar not null default '',
    owner varchar not null,

    in_item varchar not null,
    in_count int not null,

    out_item varchar not null,
    out_count int not null,

    out_stock int not null, -- in multiples of out_count 0=out of stock
    active boolean not null,


    -- block coordinates
    x int not null,
    y int not null,
    z int not null,

    -- mapblock coordinates
    posx int not null,
    posy int not null,
    posz int not null,

    mtime bigint not null

);

create index SHOP_MAPBLOCK on shop(posx, posy, posz);