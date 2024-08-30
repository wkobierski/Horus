package v2Iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

interface Cabinet {
    // zwraca dowolny element o podanej nazwie
    Optional<Folder> findFolderByName(String name);

    // zwraca wszystkie foldery podanego rozmiaru SMALL/MEDIUM/LARGE
    List<Folder> findFoldersBySize(String size);

    //zwraca liczbę wszystkich obiektów tworzących strukturę
    int count();
}

public class FileCabinet implements Cabinet, Iterable<Folder> {
    private List<Folder> folders;

    /*
    jednowymiarowa lista folderów, ułatwiająca iterowanie przez v2Iterator.FileCabinet
    jeśli w v2Iterator.FileCabinet byłaby metoda np. addFolders(), musiała by również uwzględniać dodanie tych folderów
    do listy foldersFlat
     */
    private List<Folder> foldersFlat = foldersTo1DList(this.folders);

    @Override
    public Optional<Folder> findFolderByName(String name) {
        for (Folder folder : this){
            if (folder.getName().equals(name)){
                return Optional.of(folder);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Folder> findFoldersBySize(String size) {
        List<Folder> resultList = new ArrayList<>();

        for (Folder folder : this){
            if (folder.getSize().equals(size)){
                resultList.add(folder);
            }
        }

        return resultList;
    }

    @Override
    public int count() {
        return foldersFlat.size();
    }



    @Override
    public Iterator<Folder> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < foldersFlat.size();
            }

            @Override
            public Folder next() {
                return foldersFlat.get(index++);
            }
        };
    }

    private List<Folder> foldersTo1DList(List<Folder> folders){
        List<Folder> resultList = new ArrayList<>();

        if (folders == null)
            return resultList;

        for (Folder folder : folders){
            resultList.add(folder);

            if (folder instanceof MultiFolder){
                resultList.addAll(foldersTo1DList(((MultiFolder) folder).getFolders()));
            }
        }

        return resultList;
    }
}

interface Folder {
    String getName();
    String getSize();
}

interface MultiFolder extends Folder {
    List<Folder> getFolders();
}