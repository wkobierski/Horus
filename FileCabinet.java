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

    //zwraca pierwszy napotkany folder o zadanej nazwie
    @Override
    public Optional<Folder> findFolderByName(String name) {
        for (Folder folder : this) {
            if (folder.getName().equals(name)) {
                return Optional.of(folder);
            }
        }

        return Optional.empty();
    }

    //zwraca listę folderów o zadanym rozmiarze
    @Override
    public List<Folder> findFoldersBySize(String size) {
        List<Folder> resultList = new ArrayList<>();

        for (Folder folder : this) {
            if (folder.getSize().equals(size)) {
                resultList.add(folder);
            }
        }

        return resultList;
    }

    //zwraca całkowitą liczbę folderów w strukturze
    @Override
    public int count() {
        int count = 0;

        for (Folder ignored : this) {
            count++;
        }

        return count;
    }

    //dodaje możliwość iterowania po naszym FileCabinet dzięki iteratorowi FolderIterator
    @Override
    public Iterator<Folder> iterator() {
        return new FolderIterator(this.folders);
    }

    //iterator pozwalający rekurencyjnie przejść po wszystkich folderach w FileCabinet
    private static class FolderIterator implements Iterator<Folder> {
        private final Iterator<Folder> currentIterator;
        private FolderIterator subFolderIterator;
        private Folder nextFolder;

        public FolderIterator(List<Folder> rootFolders) {
            this.currentIterator = rootFolders != null ? rootFolders.iterator() : null;
            this.nextFolder = findNext();
        }

        @Override
        public boolean hasNext() {
            return nextFolder != null;
        }

        @Override
        public Folder next() {
            Folder folderToReturn = nextFolder;
            nextFolder = findNext();

            return folderToReturn;
        }

        private Folder findNext() {
            // sprawdzenie czy istnieje podfolder do przeiterowania i czy ma on kolejne elementy
            if (subFolderIterator != null && subFolderIterator.hasNext()) {
                return subFolderIterator.next();
            }
            subFolderIterator = null;

            // sprawdzenie czy na tym poziomie są jeszcze foldery do iterowania
            if (currentIterator != null && currentIterator.hasNext()) {
                Folder currentFolder = currentIterator.next();

                /* jeżeli folder to Multifolder, który zawiera w sobie podfoldery,
                    to rekurencyjnie tworzymy iterator po jego podfolderach
                 */
                if (currentFolder instanceof MultiFolder) {
                    if (!((MultiFolder) currentFolder).getFolders().isEmpty()) {
                        subFolderIterator = new FolderIterator(((MultiFolder) currentFolder).getFolders());
                    }
                }

                return currentFolder;
            }

            return null; //jeżeli nie ma więcej folderów na danym poziomie lub niżej to zwracamy null
        }
    }
}

interface Folder {
    String getName();

    String getSize();
}

interface MultiFolder extends Folder {
    List<Folder> getFolders();
}