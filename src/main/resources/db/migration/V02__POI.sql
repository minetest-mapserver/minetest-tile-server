
create table poi(
    id serial not null,

    name varchar not null,
    category varchar not null,
    owner varchar not null,
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

create index POI_MAPBLOCK on poi(posx, posy, posz);