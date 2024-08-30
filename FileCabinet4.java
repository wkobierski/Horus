package v4LepszyIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

interface Cabinet {
    Optional<Folder> findFolderByName(String name);
    List<Folder> findFoldersBySize(String size);
    int count();
}

public class FileCabinet implements Cabinet, Iterable<Folder> {
    private List<Folder> folders;

    @Override
    public Optional<Folder> findFolderByName(String name) {
        for (Folder folder : this) {
            if (folder.getName().equals(name)) {
                return Optional.of(folder);
            }
        }
        return Optional.empty();
    }

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

    @Override
    public int count() {
        int count = 0;
        for (Folder folder : this) {
            count++;
        }

        return count;
    }

    @Override
    public Iterator<Folder> iterator() {
        return new FolderIterator(folders);
    }

    private class FolderIterator implements Iterator<Folder> {
        private Iterator<Folder> currentIterator;
        private FolderIterator subFolderIterator;
        private Folder nextFolder;

        public FolderIterator(List<Folder> rootFolders) {
            this.currentIterator = rootFolders != null ? rootFolders.iterator() : null;
            this.nextFolder = advance();
        }

        @Override
        public boolean hasNext() {
            return nextFolder != null;
        }

        @Override
        public Folder next() {
            Folder toReturn = nextFolder;
            nextFolder = advance();

            return toReturn;
        }

        private Folder advance() {
            // Check if there is a subfolder iterator that is not yet exhausted
            if (subFolderIterator != null && subFolderIterator.hasNext()) {
                return subFolderIterator.next();
            }
            subFolderIterator = null;

            // Iterate over the current level folders
            while (currentIterator != null && currentIterator.hasNext()) {
                Folder currentFolder = currentIterator.next();

                // If it's a MultiFolder, create a new iterator for its subfolders
                if (currentFolder instanceof MultiFolder) {
                    subFolderIterator = new FolderIterator(((MultiFolder) currentFolder).getFolders());
                    if (subFolderIterator.hasNext()) {
                        return currentFolder;
                    }
                } else {
                    return currentFolder;
                }
            }
            return null; // No more folders
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