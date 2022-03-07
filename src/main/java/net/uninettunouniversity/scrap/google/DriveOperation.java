package net.uninettunouniversity.scrap.google;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveOperation extends GoogleOperationAbstract {
	private Drive service;

	public DriveOperation() throws GeneralSecurityException, IOException {
		service = getDriveService();
	}

	public List<File> listFiles(String folderId) throws IOException, GeneralSecurityException {

		FileList result = service.files().list().setQ("'" + folderId + "' in parents")
				.setFields("nextPageToken, files(id, name)").execute();
		return result.getFiles();

	}

	public InputStream getContentFile(com.google.api.services.drive.model.File file)
			throws GeneralSecurityException, IOException {
		return service.files().get(file.getId()).executeMediaAsInputStream();
	}

	public String upload(java.io.File file, String folderId) throws GeneralSecurityException, IOException {
		File fileMetadata = new File();
		fileMetadata.setName(file.getName());
		fileMetadata.setParents(Collections.singletonList(folderId));

		MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
		String mimeType = fileTypeMap.getContentType(file.getName());

		FileContent mediaContent = new FileContent(mimeType, file);

		File uploadedFile = service.files().create(fileMetadata, mediaContent).setFields("id").execute();

		return uploadedFile.getId();
	}

	public String newFolder(String folderName, String parentId) throws GeneralSecurityException, IOException {

		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setParents(Collections.singletonList(parentId));
		fileMetadata.setMimeType("application/vnd.google-apps.folder");

		File file = service.files().create(fileMetadata).setFields("id").execute();

		return file.getId();
	}

	private Drive getDriveService() throws GeneralSecurityException, IOException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getHttpRequestInitializer())
				.setApplicationName(APPLICATION_NAME).build();
	}

	public String newGoogleSpreadsheet(String name, String parentId) throws GeneralSecurityException, IOException {

		File fileMetadata = new File();
		fileMetadata.setName(name);
		fileMetadata.setParents(Collections.singletonList(parentId));
		fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

		File file = service.files().create(fileMetadata).setFields("id").execute();

		return file.getId();
	}

	public void deleteFile(String fileId) throws GeneralSecurityException, IOException {
		service.files().delete(fileId).execute();
	}

}
