package v1Searchingin1;

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
        NAME, SIZE, COUNT
    }


    //zwraca dowolny folder o podanej nazwie lub Optional.empty jeżeli nie znaleziono żadnego folderu o podanej nazwie
    @Override
    public Optional<Folder> findFolderByName(String name) {
        List<Folder> resultList = exploreFolders(this.folders, SearchType.NAME, name, null);

        return resultList.isEmpty() ? Optional.empty() : Optional.ofNullable(resultList.getFirst());
    }

    //zwraca wszystkie foldery o podanym rozmiarze
    @Override
    public List<Folder> findFoldersBySize(String size) {
        return exploreFolders(this.folders, SearchType.SIZE, null, size);
    }

    //zwraca liczbę wszystkich obiektów tworzących strukturę
    @Override
    public int count() {
        return exploreFolders(this.folders, SearchType.COUNT, null, null).size();
    }


    /*przeszukuje foldery w poszukiwaniu tych, które spełniają podany warunek
    jeżeli napotkany folder jest Multifolderem, metoda rekurencyjnie przeszukuje Multifolder
     */
    private List<Folder> exploreFolders(List<Folder> folders, SearchType searchType, String name, String size){
        List<Folder> resultList = new ArrayList<>();

        if (folders == null)
            return resultList;

        for (Folder folder : folders){
            if(folderMeetsCondition(folder, searchType, name, size)){
                resultList.add(folder);
            }

            if(searchType == SearchType.NAME && !resultList.isEmpty())
                return resultList;

            if (folder instanceof MultiFolder){
                resultList.addAll(exploreFolders(((MultiFolder) folder).getFolders(), searchType, name, size));
            }
        }

        return resultList;
    }

    //sprawdzenie warunku dla folderu w metodzie exploreFolders, w zależności od rodzaju przeszukiwania
    private boolean folderMeetsCondition(Folder folder, SearchType searchType, String name, String size) {
        return switch (searchType) {
            case NAME -> folder.getName().equals(name);
            case SIZE -> folder.getSize().equals(size);
            case COUNT -> true;
        };
    }

}

interface Folder {
    String getName();
    String getSize();
}

interface MultiFolder extends Folder {
    List<Folder> getFolders();
}

