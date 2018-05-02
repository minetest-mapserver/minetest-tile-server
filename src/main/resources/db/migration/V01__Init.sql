
create table tileserver_block_changes (
	posx int not null,
	posy int not null,
	posz int not null,
	
	-- changed flag, true if updated or inserted
	changed boolean default false,
	
	PRIMARY KEY (posX,posY,posZ)
);

create table tileserver_block_depth (
	posx int not null,
	posz int not null,
	
	-- visible y-depth, inclusive
	visibley int not null,
	
	PRIMARY KEY (posX,posZ)
);

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
    insert into tileserver_block_changes(posx, posy, posz, changed)
    values(NEW.posx, NEW.posy, NEW.posz, true)
    on conflict (posx, posy, posz) do update set changed = excluded.changed;

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

