/*
 * This file is generated by jOOQ.
*/
package io.rudin.minetest.tileserver.blockdb.tables.records;


import io.rudin.minetest.tileserver.blockdb.tables.Shop;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record16;
import org.jooq.Row16;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShopRecord extends TableRecordImpl<ShopRecord> implements Record16<Integer, String, String, String, Integer, String, Integer, Integer, Boolean, Integer, Integer, Integer, Integer, Integer, Integer, Long> {

    private static final long serialVersionUID = -1377588995;

    /**
     * Setter for <code>shop.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>shop.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>shop.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>shop.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>shop.owner</code>.
     */
    public void setOwner(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>shop.owner</code>.
     */
    public String getOwner() {
        return (String) get(2);
    }

    /**
     * Setter for <code>shop.in_item</code>.
     */
    public void setInItem(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>shop.in_item</code>.
     */
    public String getInItem() {
        return (String) get(3);
    }

    /**
     * Setter for <code>shop.in_count</code>.
     */
    public void setInCount(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>shop.in_count</code>.
     */
    public Integer getInCount() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>shop.out_item</code>.
     */
    public void setOutItem(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>shop.out_item</code>.
     */
    public String getOutItem() {
        return (String) get(5);
    }

    /**
     * Setter for <code>shop.out_count</code>.
     */
    public void setOutCount(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>shop.out_count</code>.
     */
    public Integer getOutCount() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>shop.out_stock</code>.
     */
    public void setOutStock(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>shop.out_stock</code>.
     */
    public Integer getOutStock() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>shop.active</code>.
     */
    public void setActive(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>shop.active</code>.
     */
    public Boolean getActive() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>shop.x</code>.
     */
    public void setX(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>shop.x</code>.
     */
    public Integer getX() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>shop.y</code>.
     */
    public void setY(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>shop.y</code>.
     */
    public Integer getY() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>shop.z</code>.
     */
    public void setZ(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>shop.z</code>.
     */
    public Integer getZ() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>shop.posx</code>.
     */
    public void setPosx(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>shop.posx</code>.
     */
    public Integer getPosx() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>shop.posy</code>.
     */
    public void setPosy(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>shop.posy</code>.
     */
    public Integer getPosy() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>shop.posz</code>.
     */
    public void setPosz(Integer value) {
        set(14, value);
    }

    /**
     * Getter for <code>shop.posz</code>.
     */
    public Integer getPosz() {
        return (Integer) get(14);
    }

    /**
     * Setter for <code>shop.mtime</code>.
     */
    public void setMtime(Long value) {
        set(15, value);
    }

    /**
     * Getter for <code>shop.mtime</code>.
     */
    public Long getMtime() {
        return (Long) get(15);
    }

    // -------------------------------------------------------------------------
    // Record16 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Integer, String, String, String, Integer, String, Integer, Integer, Boolean, Integer, Integer, Integer, Integer, Integer, Integer, Long> fieldsRow() {
        return (Row16) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row16<Integer, String, String, String, Integer, String, Integer, Integer, Boolean, Integer, Integer, Integer, Integer, Integer, Integer, Long> valuesRow() {
        return (Row16) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Shop.SHOP.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Shop.SHOP.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Shop.SHOP.OWNER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Shop.SHOP.IN_ITEM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Shop.SHOP.IN_COUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Shop.SHOP.OUT_ITEM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return Shop.SHOP.OUT_COUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return Shop.SHOP.OUT_STOCK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field9() {
        return Shop.SHOP.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field10() {
        return Shop.SHOP.X;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return Shop.SHOP.Y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return Shop.SHOP.Z;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return Shop.SHOP.POSX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return Shop.SHOP.POSY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field15() {
        return Shop.SHOP.POSZ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field16() {
        return Shop.SHOP.MTIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getOwner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getInItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getInCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getOutItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getOutCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getOutStock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component9() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component10() {
        return getX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component11() {
        return getY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component12() {
        return getZ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component13() {
        return getPosx();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component14() {
        return getPosy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component15() {
        return getPosz();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component16() {
        return getMtime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getOwner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getInItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getInCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getOutItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getOutCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getOutStock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value9() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value10() {
        return getX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value12() {
        return getZ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getPosx();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getPosy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value15() {
        return getPosz();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value16() {
        return getMtime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value3(String value) {
        setOwner(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value4(String value) {
        setInItem(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value5(Integer value) {
        setInCount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value6(String value) {
        setOutItem(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value7(Integer value) {
        setOutCount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value8(Integer value) {
        setOutStock(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value9(Boolean value) {
        setActive(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value10(Integer value) {
        setX(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value11(Integer value) {
        setY(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value12(Integer value) {
        setZ(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value13(Integer value) {
        setPosx(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value14(Integer value) {
        setPosy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value15(Integer value) {
        setPosz(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord value16(Long value) {
        setMtime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShopRecord values(Integer value1, String value2, String value3, String value4, Integer value5, String value6, Integer value7, Integer value8, Boolean value9, Integer value10, Integer value11, Integer value12, Integer value13, Integer value14, Integer value15, Long value16) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ShopRecord
     */
    public ShopRecord() {
        super(Shop.SHOP);
    }

    /**
     * Create a detached, initialised ShopRecord
     */
    public ShopRecord(Integer id, String name, String owner, String inItem, Integer inCount, String outItem, Integer outCount, Integer outStock, Boolean active, Integer x, Integer y, Integer z, Integer posx, Integer posy, Integer posz, Long mtime) {
        super(Shop.SHOP);

        set(0, id);
        set(1, name);
        set(2, owner);
        set(3, inItem);
        set(4, inCount);
        set(5, outItem);
        set(6, outCount);
        set(7, outStock);
        set(8, active);
        set(9, x);
        set(10, y);
        set(11, z);
        set(12, posx);
        set(13, posy);
        set(14, posz);
        set(15, mtime);
    }
}
