package v3SimpleZPowielaniem;

import java.util.ArrayList;
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

public class FileCabinet implements Cabinet {
    private List<Folder> folders;

    private enum SearchType{
        SIZE, COUNT
    }

    @Override
    public Optional<Folder> findFolderByName(String name) {
        return findFolderByName(name, this.folders);
    }

    private Optional<Folder> findFolderByName(String name, List<Folder> folders){
        for (Folder folder : folders){
            if (folder.getName().equals(name)){
                return Optional.of(folder);
            }

            if(folder instanceof MultiFolder){
                return findFolderByName(name, ((MultiFolder) folder).getFolders());
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Folder> findFoldersBySize(String size) {
        return searchAllFolders(this.folders, SearchType.SIZE, size);
    }

    @Override
    public int count() {
        return searchAllFolders(this.folders, SearchType.COUNT, "").size();
    }

    private List<Folder> searchAllFolders (List<Folder> folders, SearchType searchType, String size){
        List<Folder> resultList = new ArrayList<>();

        for (Folder folder : folders){
            switch(searchType){
                case SIZE:
                    if (folder.getSize().equals(size)){
                        resultList.add(folder);
                    }
                    break;

                case COUNT:
                    resultList.add(folder);
            }

            if(folder instanceof MultiFolder){
                resultList.addAll(searchAllFolders(((MultiFolder) folder).getFolders(), searchType, size));
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