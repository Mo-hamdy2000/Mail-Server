package Classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;

import Interfaces.IFolder;
import Misc.Birthday;

public class Folder implements IFolder {

    //FolderPath member stores the folder path on the system
    private final String path;

    /**
     * class constructor
     *
     * @param path the path of the folder on the system
     */
    public Folder(String path) {
        this.path = path;
    }

    //folder separator according to the OS
    private static final String sep = System.getProperty("file.separator");

    /**
     * the method creates a folder in <project root>/system/users with its name same as user's address
     * it then creates all necessary sub-folders and files and fills them with information if necessary
     *
     * @param address user's address
     * @param name    user's name
     * @param gender  user's gender
     * @param bd      user's birthday
     * @throws IOException folder cannot be created
     */
    public void createUserFolder(String address, String name, String gender, Birthday bd) throws IOException {
        Folder userFolder = addSubFolder(address);
        File userInfo = new File(userFolder.getPath() + sep + "info.txt");
        userInfo.createNewFile();
        File contacts = new File(userFolder.getPath() + sep + "contacts.csv");
        contacts.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(userInfo, false));
        writer.write(name);
        writer.newLine();
        writer.write(gender);
        writer.newLine();
        writer.write(Integer.toString(bd.getDay()));
        writer.newLine();
        writer.write(Integer.toString(bd.getMonth()));
        writer.newLine();
        writer.write(Integer.toString(bd.getYear()));
        writer.newLine();
        writer.close();
        Folder inbox = userFolder.addSubFolder("inbox");
        File inboxIndex = new File(inbox.getPath() + sep + "index.csv");
        inboxIndex.createNewFile();
        File inboxFolders = new File(inbox.getPath() + sep + "folders.txt");
        inboxFolders.createNewFile();
        Folder archive = userFolder.addSubFolder("archive");
        File archiveIndex = new File(archive.getPath() + sep + "index.csv");
        archiveIndex.createNewFile();
        Folder sent = userFolder.addSubFolder("sent");
        File sentIndex = new File(sent.getPath() + sep + "index.csv");
        sentIndex.createNewFile();
        Folder trash = userFolder.addSubFolder("trash");
        File trashIndex = new File(trash.getPath() + sep + "index.csv");
        trashIndex.createNewFile();
        Folder drafts = userFolder.addSubFolder("drafts");
        File draftsIndex = new File(drafts.getPath() + sep + "index.csv");
        draftsIndex.createNewFile();

    }

    /**
     * @return the folder's name i.e. the address of the user owning this folder
     */
    public String folderName() {
        //splits the path of the folder
        String[] names = splitPath(this.path);
        return names[names.length - 1];
    }

    /**
     * used by the above method
     *
     * @param pathString to be splitted
     * @return array containing the path after being split
     */
    private static String[] splitPath(String pathString) {
        Path path = Paths.get(pathString);
        return StreamSupport.stream(path.spliterator(), false).map(Path::toString)
                .toArray(String[]::new);
    }

    /**
     * adds a sub-folder to this folder
     *
     * @param name name of the new sub-folder
     * @return the new Folder object
     */
    protected Folder addSubFolder(String name) {
        File f = new File(path + sep + name);
        f.mkdir();
        return new Folder(path + sep + name);
    }

    /**
     * @param from
     * @param desPath
     * @return
     */
    public static boolean copyFiles(File from, String desPath) {
        try {
            byte[] fileContent = Files.readAllBytes(from.toPath());
            File out = new File(desPath);
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(fileContent);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
			return false;
		}
		
    }

    public static void deleteMailFolder(String path) {
        Folder currentFolder = new Folder(path);
        File file = new File(currentFolder.getPath() + sep + "attachment");
        if (file.exists()) {
            String[] files = file.list();
            for (String pathname : files) {
                File srcFile = new File(currentFolder.getPath() + sep + "attachment" + sep + pathname);
                srcFile.delete();
            }
            File originalAttachmentFolder = new File(currentFolder.getPath() + sep + "attachment");
            originalAttachmentFolder.delete();
        }
        // deleting original files
        File originalFile = new File(currentFolder.getPath() + sep + currentFolder.folderName() + ".txt");
        originalFile.delete();
        File originalFolder = new File(currentFolder.getPath());
        originalFolder.delete();
    }

    /**
     * @return path of the folder
     */
    public String getPath() {
        return path;
    }

    /**
     * used if this folder contains index file, inbox folder for example.
     *
     * @return path of the index file
     */
    protected String getIndexPath() {
        return path + sep +"index.csv";
	}
	
}
