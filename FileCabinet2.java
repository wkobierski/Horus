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

public class FileCabinet2 implements Cabinet, Iterable<Folder> {
    private List<Folder> folders;

    /*
    jednowymiarowa lista folderów, ułatwiająca iterowanie przez FileCabinet
    jeśli w FileCabinet byłaby metoda np. addFolders(), musiała by również uwzględniać dodanie tych folderów
    do listy foldersFlat
     */
    private List<Folder> foldersFlat = foldersToOneDimensionList(this.folders);

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

    private List<Folder> foldersToOneDimensionList(List<Folder> folders){
        List<Folder> resultList = new ArrayList<>();

        if (folders == null)
            return resultList;

        for (Folder folder : folders){
            resultList.add(folder);

            if (folder instanceof MultiFolder){
                resultList.addAll(foldersToOneDimensionList(((MultiFolder) folder).getFolders()));
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

//    //zwraca dowolny folder o podanej nazwie lub Optional.empty jeżeli nie znaleziono żadnego folderu o podanej nazwie
//    @Override
//    public Optional<Folder> findFolderByName(String name) {
//        List<Folder> resultList = exploreFolders(this.folders, FileCabinet.SearchType.NAME, name, null);
//
//        return resultList.isEmpty() ? Optional.empty() : Optional.ofNullable(resultList.getFirst());
//    }
//
//    //zwraca wszystkie foldery o podanym rozmiarze
//    @Override
//    public List<Folder> findFoldersBySize(String size) {
//        return exploreFolders(this.folders, FileCabinet.SearchType.SIZE, null, size);
//    }
//
//    //zwraca liczbę wszystkich obiektów tworzących strukturę
//    @Override
//    public int count() {
//        return exploreFolders(this.folders, FileCabinet.SearchType.COUNT, null, null).size();
//    }
//
//
//    /*przeszukuje foldery w poszukiwaniu tych, które spełniają podany warunek
//    jeżeli napotkany folder jest Multifolderem, metoda rekurencyjnie przeszukuje Multifolder
//     */
//    private List<Folder> exploreFolders(List<Folder> folders, FileCabinet.SearchType searchType, String name, String size){
//        List<Folder> resultList = new ArrayList<>();
//
//        if (folders == null)
//            return resultList;
//
//        for (Folder folder : folders){
//            if(folderMeetsCondition(folder, searchType, name, size)){
//                resultList.add(folder);
//            }
//
//            if(searchType == FileCabinet.SearchType.NAME && !resultList.isEmpty())
//                return resultList;
//
//            if (folder instanceof MultiFolder){
//                resultList.addAll(exploreFolders(((MultiFolder) folder).getFolders(), searchType, name, size));
//            }
//        }
//
//        return resultList;
//    }
//
//    //sprawdzenie warunku dla folderu w metodzie exploreFolders, w zależności od rodzaju przeszukiwania
//    private boolean folderMeetsCondition(Folder folder, FileCabinet.SearchType searchType, String name, String size) {
//        return switch (searchType) {
//            case NAME -> folder.getName().equals(name);
//            case SIZE -> folder.getSize().equals(size);
//            case COUNT -> true;
//        };
//    }