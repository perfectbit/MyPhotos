package myphotos.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;

/**
 * The Class DBProvider. Create connection to database and implements interface
 * with database
 */
public class DBProvider {
	private String dbURL = "jdbc:derby:MyDB;create=true";// user=user;password=password";
	private Connection mConn = null;
	private Statement mStmt = null;

	public DBProvider() {
		File cacheDirect = new File("imgcache");
		if (!cacheDirect.exists()) cacheDirect.mkdir();
		
		createConnection(); // done
		// dropTables(); // done
		// createTables(); // done
		checkTables(); // done
		addNewTag("cats"); // done
		// selectTag(newTag); // done
		showTableTags(); // done
		// insertImage("image2.jpg", "c://img//image2.jpg", selectTag("cats"));
		showTableImages(); // done
		// checkTables();
		// updateImage();
		// updateTag(newTag, "Valera");
	}

	public void changeTagName(String oldTag, String newTag) {
		if (oldTag.equals("--New--")) {
			return;
		}
		String strUpdate = "UPDATE TAGS SET tag = \'" + newTag + "\' "
				+ "WHERE tag = \'" + oldTag + "\'";
		try {
			if (selectTag(oldTag) < 0) {
				System.out.println("Can't update tag. No such tag in table");
				return;
			}
			mStmt = mConn.createStatement();
			mStmt.execute(strUpdate);
			mStmt.close();
			System.out.println("Tag name updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean addNewTag(String newTag) {
		if (newTag == null) {
			System.out.println("Can't add \"null\" tag.");
			return false;
		}
		if (newTag.endsWith("\'") || newTag.startsWith("\'")) {
			System.out.println("Can't add tag starting with \" \' \" or ending with \" \' \" .");
			return false;
		}
		if (newTag.equals("")) {
			System.out.println("Can't add tag \"\".");
			return false;
		}
		String strSelectCheck = "SELECT TAG FROM TAGS WHERE TAG = \'" + newTag
				+ "\'";
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectCheck);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				System.out.println("New tag is not unique, can't add new tag.");
				return false;
			}
			mStmt.execute("insert into APP.tags " + "(tag) values (\'" + newTag
					+ "\')");
			mStmt.close();
			System.out.println("New tag added");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public int deleteTag(String delTag) {
		if (delTag.equals("--New--")) {
			return -1;
		}
		String selectTag = "SELECT TAGS_PK FROM TAGS WHERE TAG =\'" + delTag
				+ "\'";
		String strDeleteTag = "DELETE FROM TAGS WHERE TAG = \'" + delTag + "\'";
		int tags_pk = -1;
		try {
			ResultSet result;
			mStmt = mConn.createStatement();
			mStmt.execute(selectTag);
			result = mStmt.getResultSet();
			while (result.next()) {
				tags_pk = result.getInt("TAGS_PK");
			}
			mStmt.execute(strDeleteTag);
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return tags_pk;
		}
		return tags_pk;
	}

	public void updateImagesTag(int currentTagsPK, int newTagsPK) {
		String strUpdateTag = "UPDATE IMAGES SET TAGS_PK = " + newTagsPK
				+ " WHERE TAGS_PK = " + currentTagsPK;
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strUpdateTag);
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateImages() {

	}

	private void showTableImages() {
		String strSelectImagesTable = "SELECT * FROM APP.IMAGES";
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectImagesTable);
			ResultSet result = mStmt.getResultSet();
			System.out.println("All records from table \"Images\"");
			while (result.next()) {
				System.out.println(result.getString("IMAGES_PK") + " - "
						+ result.getString("NAMEOFFILE") + " - "
						+ result.getString("FULLNAME") + " - "
						+ result.getString("TAGS_PK") + " - "
						+ result.getString("IMAGECACHE"));
			}
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showTableTags() {
		String strSelectTagsTable = "SELECT * FROM APP.TAGS";
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectTagsTable);
			ResultSet result = mStmt.getResultSet();
			System.out.println("All records from table \"Tags\"");
			while (result.next()) {
				System.out.println(result.getString("TAGS_PK") + " - "
						+ result.getString("TAG"));
			}
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int selectTag(String tagName) {
		String strSelectTag = "SELECT * FROM APP.TAGS WHERE tag = \'" + tagName
				+ "\'";
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectTag);
			ResultSet result = mStmt.getResultSet();
			System.out.println("Selected records from table \"Tags\" ");
			int tags_pk = -1;
			while (result.next()) {
				System.out.println(result.getString("TAGS_PK") + " - "
						+ result.getString("TAG"));
				tags_pk = result.getInt("TAGS_PK");
			}
			mStmt.close();
			return tags_pk;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	/**
	 * Check tables.
	 */
	private void checkTables() {
		boolean bTableImages = false;
		boolean bTableTags = false;
		try {
			DatabaseMetaData meta = mConn.getMetaData();
			ResultSet res = meta.getTables(null, null, null,
					new String[] { "TABLE" });
			while (res.next()) {
				// System.out.println(res.getString("TABLE_CAT") + ", "
				// + res.getString("TABLE_SCHEM") + ", "
				// + res.getString("TABLE_NAME"));
				if (res.getString("TABLE_NAME").equals("IMAGES")) {
					bTableImages = true;
					System.out.println("Table \"IMAGES\" exist");
				}
				if (res.getString("TABLE_NAME").equals("TAGS")) {
					bTableTags = true;
					System.out.println("Table \"TAGS\" exist");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (!bTableTags || !bTableImages) {
			dropTables();
			createTables();
		}
	}

	/**
	 * Drop tables APP.TAGS and APP.IMAGES
	 */
	private void dropTables() {
		String strDropTableTags = "DROP TABLE TAGS";
		String strDropTableImages = "DROP TABLE IMAGES";

		boolean flag1 = false;
		boolean flag2 = false;
		try {
			mStmt = mConn.createStatement();
			DatabaseMetaData meta = mConn.getMetaData();
			ResultSet res = meta.getTables(null, null, null,
					new String[] { "TABLE" });
			while (res.next()) {
				if (res.getString("TABLE_NAME").equals("IMAGES")) {
					flag1 = true;
					System.out.println("IMAGES exist");
				}
				if (res.getString("TABLE_NAME").equals("TAGS")) {
					flag2 = true;
					System.out.println("TAGS exist");
				}
			}
			if (flag1) {
				mStmt.execute(strDropTableImages);
				System.out.println("IMAGES deleted");
			}
			if (flag2) {
				mStmt.execute(strDropTableTags);
				System.out.println("TAGS deleted");
			}
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTables() {
		String strStateCreateTagsTable = "CREATE table TAGS ("
				+ "tags_pk INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT tags_pk PRIMARY KEY, "
				+ "TAG varchar(255) UNIQUE)";
		String strStateCreateImagesTable = "CREATE table IMAGES ("
				+ "images_pk INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT images_pk PRIMARY KEY, "
				+ "NAMEOFFILE varchar(255) NOT NULL, "
				+ "FULLNAME	varchar(255) NOT NULL UNIQUE, "
				+ "tags_pk INTEGER NOT NULL, " + "IMAGECACHE varchar(255), "
				+ "FOREIGN KEY (tags_pk) REFERENCES TAGS(tags_pk))";
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strStateCreateTagsTable);
			mStmt.execute(strStateCreateImagesTable);
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		addNewTag("--New--");
		System.out.println("New Tables created");
	}

	private void createConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			mConn = DriverManager.getConnection(dbURL);// , "user",
			// "password");
		} catch (Exception except) {
			except.printStackTrace();
		}
		System.out.println("Connected to database");
	}

	public int insertImage(String nameOfFile, String fullName, int tags_pk) {
		int image_pk = -1;
		String strSelectFullName = "SELECT * FROM APP.IMAGES WHERE FULLNAME = "
				+ "\'" + fullName + "\'";
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectFullName);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				System.out
						.println("New NameOfFile is not unique, can't add new image");
				return image_pk;
			}
			mStmt.execute("insert into APP.images "
					+ "(NAMEOFFILE, FULLNAME, TAGS_PK, IMAGECACHE)"
					+ " values (" + "\'" + nameOfFile + "\', " + "\'"
					+ fullName + "\', " + tags_pk + ", \'null\'" + ")");
			mStmt.execute("SELECT IMAGES_PK FROM IMAGES WHERE NAMEOFFILE = \'"
					+ nameOfFile + "\'");

			String imageCache = null;
			result = mStmt.getResultSet();

			while (result.next()) {
				System.out.println("IMAGES_PK = " + result.getString("IMAGES_PK"));
				imageCache = result.getString("IMAGES_PK");
				image_pk = result.getInt("IMAGES_PK");
			}
			mStmt.execute("UPDATE IMAGES SET IMAGECACHE = \'" + imageCache
					+ "_" + nameOfFile + "\' " + "WHERE NAMEOFFILE = \'"
					+ nameOfFile + "\'");
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return image_pk;
	}

	/**
	 * Close DB connection.
	 */
	public void shutdown() {
		try {
			if (mStmt != null) {
				mStmt.close();
			}
			if (mConn != null) {
				DriverManager.getConnection(dbURL + ";shutdown=true");
				mConn.close();
			}
		} catch (SQLException e) {
			System.out.println("SQLExecption on close DB");
		}
	}

	public String[] getTagsArray() {
		String strSelectTags = "SELECT TAG FROM APP.TAGS";
		LinkedList<String> A = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectTags);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				A.add(result.getString("TAG"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return A.toArray(new String[A.size()]);
	}

	public LinkedList<String> getTagsListFromTags() {
		String strSelectTags = "SELECT TAG FROM TAGS";
		LinkedList<String> tagList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectTags);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				tagList.add(result.getString("TAG"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tagList;
	}

	public LinkedList<String> getNameOfFile() {
		String strSelectNames = "SELECT NAMEOFFILE FROM IMAGES ORDER BY IMAGES_PK";
		LinkedList<String> nameList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				nameList.add(result.getString("NAMEOFFILE"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameList;
	}

	public LinkedList<String> getFullNameOfFile() {
		String strSelectFullNames = "SELECT FULLNAME FROM IMAGES ORDER BY IMAGES_PK";
		LinkedList<String> nameList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectFullNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				nameList.add(result.getString("FULLNAME"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameList;
	}

	public LinkedList<Integer> getTagsPK() {
		String strSelectTagsPK = "SELECT TAGS_PK FROM IMAGES";
		LinkedList<Integer> tagsPKList = new LinkedList<Integer>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectTagsPK);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				tagsPKList.add(result.getInt("TAG_PK"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tagsPKList;
	}

	public LinkedList<String> getCacheNames() {
		String strSelectCacheNames = "SELECT IMAGECACHE FROM IMAGES ORDER by IMAGES_PK";
		LinkedList<String> cacheNameList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectCacheNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				cacheNameList.add(result.getString("IMAGECACHE"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cacheNameList;
	}

	public LinkedList<String> getCacheNames(int tags_pk) {
		String strSelectCacheNames = "SELECT IMAGECACHE FROM IMAGES WHERE tags_pk = "
				+ tags_pk + " ORDER by IMAGES_PK";
		LinkedList<String> cacheNameList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectCacheNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				cacheNameList.add(result.getString("IMAGECACHE"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cacheNameList;
	}

	public String getCacheName(int images_pk) {
		String strSelectCacheNames = "SELECT IMAGECACHE FROM IMAGES WHERE images_pk = "
				+ images_pk;
		String cacheName = null;
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectCacheNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				cacheName = result.getString("IMAGECACHE");
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cacheName;
	}

	public String getCacheName(String fullName) {
		String strSelectCacheNames = "SELECT IMAGECACHE FROM IMAGES WHERE FULLNAME = \'"
				+ fullName + "\'";
		String cacheName = null;
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectCacheNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				cacheName = result.getString("IMAGECACHE");
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cacheName;
	}

	public int getTagPK(String tag) {		
		if (tag.equals("--New--")) {
			return 1;
		}
		String strSelectTagsPK = "SELECT TAGS_PK FROM TAGS WHERE TAG = \'"
				+ tag + "\'";
		int tags_pk = 0;
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectTagsPK);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				tags_pk = result.getInt("TAGS_PK");
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags_pk;
	}

	public LinkedList<String> getNameOfFile(int tags_pk) {
		String strSelectNames = "SELECT NAMEOFFILE FROM IMAGES WHERE tags_pk = "
				+ tags_pk + " ORDER BY IMAGES_PK";
		LinkedList<String> nameList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				nameList.add(result.getString("NAMEOFFILE"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameList;
	}

	public LinkedList<String> getFullNameOfFile(int tags_pk) {
		String strSelectFullNames = "SELECT FULLNAME FROM IMAGES WHERE tags_pk = "
				+ tags_pk + " ORDER BY IMAGES_PK";
		LinkedList<String> nameList = new LinkedList<String>();
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strSelectFullNames);
			ResultSet result = mStmt.getResultSet();
			while (result.next()) {
				nameList.add(result.getString("FULLNAME"));
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nameList;		
	}

	public void setTagForImage(File imgFile, String newTag) {
		String fullName = imgFile.getAbsolutePath();
		String selectImgPK = "SELECT IMAGES_PK FROM IMAGES WHERE FULLNAME = \'" + fullName + "\'";
		String selectTagPK = "SELECT TAGS_PK FROM TAGS WHERE TAG = \'" + newTag + "\'";
		
		try {
			int image_pk = 0;
			int tags_pk = 0;			
			mStmt = mConn.createStatement();
			
			mStmt.execute(selectImgPK);
			ResultSet result = mStmt.getResultSet();			
			while (result.next()) {
				image_pk = result.getInt("IMAGES_PK");
			}
			
			mStmt.execute(selectTagPK);
			result = mStmt.getResultSet();
			while (result.next()) {
				tags_pk = result.getInt("TAGS_PK");
			}
			
			mStmt.execute("UPDATE IMAGES SET TAGS_PK = " + tags_pk
				+ " WHERE IMAGES_PK = " + image_pk);
			System.out.println("Tag changed");
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getTagForImages(String fullName) {
		String strGetTagsPK = "SELECT TAGS_PK FROM IMAGES WHERE FULLNAME = \'" + fullName + "\'";
		int tags_pk = 0;		 
		String tag = null;
		try {
			mStmt = mConn.createStatement();
			mStmt.execute(strGetTagsPK);
			ResultSet result = mStmt.getResultSet();			
			while (result.next()) {
				tags_pk = result.getInt("TAGS_PK");
			}
			String strGetTag = "SELECT TAG FROM TAGS WHERE TAGS_PK = " + tags_pk; 
			mStmt.execute(strGetTag);
			result = mStmt.getResultSet();			
			while (result.next()) {
				 tag = result.getString("TAG");
			}
			result.close();
			mStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return tag;
	}
}
