
alter table blocks add column mtime timestamp;
create index BLOCKS_TIME on blocks(mtime);

create table tileserver_tiles (
    x int not null,
    y int not null,
    z int not null,
    mtime bigint not null,
    tile bytea,

    PRIMARY KEY(x,y,z)
);

create or replace function on_blocks_change() returns trigger as
$BODY$
BEGIN
    NEW.mtime = now();
    return NEW;
END;
$BODY$
LANGUAGE plpgsql;

create trigger blocks_update
 after update
 on blocks
 for each row
 execute procedure on_blocks_change();

create trigger blocks_insert
 after insert
 on blocks
 for each row
 execute procedure on_blocks_change();

