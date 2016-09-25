package com.github.nizshee.workspace;


import com.github.nizshee.exception.WorkspaceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileWorkspace implements Workspace {
    private final String directoryPath;

    public FileWorkspace(String path) throws WorkspaceException {
        this.directoryPath = path;
    }

    @Override
    public Set<String> getFiles() throws WorkspaceException {
        try {
            Path directoryPath = Paths.get(this.directoryPath);
            return Files.walk(directoryPath).filter(path -> !path.equals(directoryPath))
                    .map(path -> directoryPath.relativize(path).normalize().toString()).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new WorkspaceException(e.getMessage());
        }
    }

    @Override
    public List<String> get(String fileName) throws WorkspaceException {
        try {
            return Files.lines(Paths.get(directoryPath, fileName)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new WorkspaceException(e.getMessage());
        }
    }

    @Override
    public void create(String fileName, List<String> content) throws WorkspaceException {
        try {
            Path path = Files.createFile(Paths.get(directoryPath, fileName));
            Files.write(path, content);
        } catch (IOException e) {
            throw new WorkspaceException(e.getMessage());
        }
    }

    @Override
    public void remove(String fileName) throws WorkspaceException {
        try {
            Files.delete(Paths.get(directoryPath, fileName));
        } catch (IOException e) {
            throw new WorkspaceException(e.getMessage());
        }
    }

    @Override
    public void change(String fileName, List<String> content) throws WorkspaceException {
        try {
            Files.write(Paths.get(directoryPath, fileName), content);
        } catch (IOException e) {
            throw new WorkspaceException(e.getMessage());
        }
    }
}
