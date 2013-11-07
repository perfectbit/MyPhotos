package myphotos.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import myphotos.components.ImagePreview;
import myphotos.io.ReadImageFromDisk;
import myphotos.io.ReadImagesFromCache;
import myphotos.io.SaveImageOnDisk;

public class Model {
	private DBProvider db = null;
	
	public Model(DBProvider newDB) {
		db = newDB;
	}
	public void deleteTag(String tag) {
		//delete tag and replace it in images table on default value 0
		int tags_pk = db.deleteTag(tag);
		db.updateImagesTag(tags_pk, 0);
	}
	
	public boolean addNewTag(String tag) {
		return db.addNewTag(tag);
	}

	public void editTag(String oldTag, String newTag) {
		db.changeTagName(oldTag, newTag);
	}
	
	// add info about image in to db and to save cache on the disk
	public synchronized void addNewImage(BufferedImage img, File file) {
		//TODO do something wtih '1'
		int image_pk = db.insertImage(file.getName(), file.getAbsolutePath(), 1);
		SaveImageOnDisk saveImage = new SaveImageOnDisk(img, file, db.getCacheName(image_pk)); 
		saveImage.start();
	}
	
	public void changeImageTag() {
		
	}
	
	public LinkedList<String> getTagsList() {		
		return db.getTagsListFromTags();
	}
	
	public String[] getTagsArray() {
		return db.getTagsArray();
	}
	
	public LinkedList<ImagePreview> createPreviews() {
		LinkedList<ImagePreview> imgPreviewsList = new LinkedList<ImagePreview>();
		LinkedList<BufferedImage> cachedImages = new ReadImagesFromCache(db.getCacheNames()).read();
		LinkedList<String> fileName = db.getNameOfFile();
		LinkedList<String> fullName =  db.getFullNameOfFile();
		//LinkedList<String> tags = db.getTagsListFromImages();
		Iterator<String> iterFullName = fullName.iterator();
		//Iterator<String> iterTags = tags.iterator();
		
		for (BufferedImage img : cachedImages) {
			//TODO delete if			
			if (img == null) {
				System.out.println("img is null - this is bad");
			}
			String strFullName = iterFullName.next();
			String tag = db.getTagForImages(strFullName);
			File file = new File(strFullName);
			System.out.println("File name is " + strFullName);			
			imgPreviewsList.add(new ImagePreview(img, file, tag));
		}
		return imgPreviewsList;
	}	
	
	public void shutdown() {		
		db.shutdown();
	}
	public LinkedList<ImagePreview> createPreviews(String tag) {		
		LinkedList<ImagePreview> imgPreviewsList = new LinkedList<ImagePreview>();
		int tags_pk = db.getTagPK(tag);
		LinkedList<String> cacheNames = db.getCacheNames(tags_pk);
		LinkedList<BufferedImage> cachedImages = new ReadImagesFromCache(cacheNames).read();
		LinkedList<String> fileName = db.getNameOfFile(tags_pk);
		LinkedList<String> fullName =  db.getFullNameOfFile(tags_pk);
		//LinkedList<String> tags = db.getTagsList();
		Iterator<String> iterFullName = fullName.iterator();
		
		for (BufferedImage img : cachedImages) {
			//TODO delete if			
			if (img == null) {
				System.out.println("img is null - this is bad");
			}
			String name = iterFullName.next();
			File file = new File(name);
			System.out.println("File name is " + name);			
			imgPreviewsList.add(new ImagePreview(img, file, tag));
		}
		return imgPreviewsList;
	}
	
	public void setTagForImage(File imgFile, String newTag) {		
		db.setTagForImage(imgFile, newTag);
	}
	
}
