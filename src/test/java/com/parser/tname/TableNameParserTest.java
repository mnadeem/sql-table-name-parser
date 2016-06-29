package com.parser.tname;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public final class TableNameParserTest {
	
	private static final String SQL_SELECT_SUB_QUERY = "SELECT /*+ materialize*/ cf_strategy_id"
													   + "FROM"
													   + " ( SELECT  strat.cf_strategy_id "
													   + "   FROM cf_strategy strat,"
													   + "        struct_doc_sect_ver prodGrp"
													   + "  WHERE  strat.src_id               = prodGrp.struct_doc_sect_id"
													   + "            AND strat.src_mgr_id     = prodGrp.mgr_id"
													   + "            AND strat.src_ver_num    = prodGrp.ver_num"
													   + "           AND strat.module_type   IN ('COMPL','PRCMSTR')"
													  + ")";
	
	
	private static final String SQL_SELECT_THREE_JOIN_WITH_ALIASE = "select c.name, s.name, s.id, r.result"
													 + " from colleges c "
													 + " join students s"
													 + "   on c.id = s.college_id"
													 + " join results r"
													 + "   on s.id = r.student_id"
													 + "where c.id = 3"
													 + "  and r.dt =  to_date('22-09-2005','dd-mm-yyyy')";
	
	private static final String SQL_COMPLEX_ONE = "INSERT INTO dr_bd_static_product"
			  + "  ("
			   + "   BUNDLE_DISCOUNT_ID,"
			  + "    CATEGORY_ID,"
			  + "    PRODUCT_ID"
			 + "   )"
			  + "  ( SELECT DISTINCT ALLNDC11.BUNDLE_DISCOUNT_ID,"
			   + "     ALLNDC11.PRODUCT_ID,"
			   + "     ALLNDC11.NDC11"
			    + "  FROM ITEM ITEM"
			     + " INNER JOIN"
			     + "   (SELECT NODE.SOURCE_ID NDC11,"
			      + "    PR.PRODUCT_ID,"
			     + "     BD1.BUNDLE_DISCOUNT_ID"
			    + "    FROM DR_BUNDLE B,"
			    + "      DR_BUNDLE_DISCOUNT BD1,"
			  + "        DR_BD_PRODUCT PR,"
			   + "       map_edge_ver node"
			   + "     WHERE B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE"
			   + "     AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE"
			  + "      AND B.BUNDLE_ID             =BD1.BUNDLE_ID"
			    + "    AND B.BUNDLE_STATUS         =3"
			    + "    AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID"
			   + "     AND BD1.IS_DYNAMIC_CATEGORY!= 1"
			   + "     AND NODE.EDGE_TYPE          = 1"
			    + "      START WITH"
			    + "      ("
			    + "        NODE.DEST_ID              = PR.PRODUCT_ID"
			    + "      AND B.BUNDLE_ID             =BD1.BUNDLE_ID"
			    + "      AND B.BUNDLE_STATUS         =3"
			    + "      AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID"
			     + "     AND BD1.IS_DYNAMIC_CATEGORY!= 1"
			    + "      AND NODE.EDGE_TYPE          = 1"
			    + "      AND B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE"
			    + "      AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE"
			     + "     )"
			   + "       CONNECT BY ( PRIOR NODE.SOURCE_ID=NODE.DEST_ID"
			    + "    AND PRIOR NODE.EDGE_TYPE           = 1"
			    + "    AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE"
			     + "   AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE"
			    + "    AND prior bd1.bundle_discount_id= bd1.bundle_discount_id)"
			    + "    ) ALLNDC11"
			    + "  ON (ALLNDC11.NDC11 = ITEM.CAT_MAP_ID)"
			    + "  UNION"
			     + "   ( SELECT BD1.BUNDLE_DISCOUNT_ID,"
			    + "      PR.PRODUCT_ID,"
			     + "     ITEM.CAT_MAP_ID"
			    + "    FROM DR_BUNDLE B,"
			    + "      DR_BUNDLE_DISCOUNT BD1,"
			     + "     DR_BD_PRODUCT PR,"
			      + "    ITEM ITEM"
			    + "    WHERE B.BUNDLE_ID           =BD1.BUNDLE_ID"
			     + "   AND B.BUNDLE_STATUS         =3"
			     + "   AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID"
			    + "    AND BD1.IS_DYNAMIC_CATEGORY!= 1"
			     + "   AND item.cat_map_id         =pr.product_id"
			    + "    )";

	@Test
	public void testSelectOneTable() {
		String sql = "SELECT name, age FROM table1 group by xyx";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1")));
	}
	
	@Test
	public void testSelectTwoTables() {
		String sql = "SELECT name, age FROM table1,table2 group by xyx";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2")));
	}
	
	@Test
	public void testSelectThreeTables() {
		String sql = "SELECT name, age FROM table1,table2,table3 group by xyx";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2","table3")));
	}
	
	@Test
	public void testSelectOneTableWithAliase() {
		String sql = "SELECT name, age FROM table1 t1 whatever group by xyx";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1")));
	}
	
	@Test
	public void testSelectTwoTablesWithAliase() {
		String sql = "SELECT name, age FROM table1 t1,table2 t2 whatever group by xyx";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2")));
	}
	
	@Test
	public void testSelectThreeTablesWithAliase() {
		String sql = "SELECT name, age FROM table1 t1,table2 t2, table3 t3 whatever group by xyx";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2", "table3")));
	}
	
	
	@Test
	public void testSelectWithSubQuery() {
		assertThat(new TableNameParser(SQL_SELECT_SUB_QUERY).tables(), equalTo(asSet("cf_strategy", "struct_doc_sect_ver")));
	}
	
	@Test
	public void testSelectWithOneJoin() {
		String sql = "SELECT coluname(s) FROM table1 join table2 ON table1.coluname=table2.coluname";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2")));
	}
	
	@Test
	public void testSelectOneJoinWithAliase() {
		String sql = "SELECT coluname(s) FROM table1 t1 join table2 t2 ON t1.coluname=t2.coluname";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2")));
	}
	
	@Test
	public void testSelectOneLeftJoin() {
		String sql = "SELECT coluname(s) FROM table1 left outer join table2 ON table1.coluname=table2.coluname";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table1", "table2")));
	}

	@Test
	public void testSelectTwoJoinWithAliase() {
		assertThat(new TableNameParser(SQL_SELECT_THREE_JOIN_WITH_ALIASE).tables(), equalTo(asSet("colleges", "students", "results")));
	}
	
	
	@Test
	public void testInsertWithValues() {
		String sql = "INSERT INTO table_name VALUES (value1,value2,value3,...)";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("table_name")));
	}
	
	@Test
	public void testInsertComplex() {
		assertThat(new TableNameParser(SQL_COMPLEX_ONE).tables(), equalTo(asSet("dr_bd_static_product", "ITEM", "DR_BUNDLE", "DR_BUNDLE_DISCOUNT", "DR_BD_PRODUCT", "map_edge_ver")));
	}
	
	@Test
	public void testInsertWithSelect() {
		String sql = "INSERT INTO Customers (CustomerName, Country) SELECT SupplierName, Country FROM Suppliers;";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("Customers", "Suppliers")));
	}

	@Test
	public void testDelete() {
		String sql = "DELETE FROM validation_task WHERE task_name = 'ValidateSoldToCustId' AND conf_id IN (SELECT conf_id FROM validation_conf WHERE conf_name IN ('SaleValidation'))";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("validation_task", "validation_conf")));
	}
	
	@Test
	public void testAlter() {
		String sql = "ALTER TABLE Persons ADD UNIQUE (P_Id)";
		assertThat(new TableNameParser(sql).tables(), equalTo(asSet("Persons")));
	}
	
	private static Collection<String> asSet(String... a) {
		Set<String> result = new HashSet<String>();
		for (String item : a) {
			result.add(item);
		}
		return result;
	}

}
