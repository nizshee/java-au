package com.github.nizshee.workspace;


import com.github.nizshee.exception.WorkspaceException;

import java.util.List;
import java.util.Set;


/**
 * Abstraction over directory with files.
 */
public interface Workspace {

    Set<String> getFiles() throws WorkspaceException;

    List<String> get(String fileName) throws WorkspaceException;

    void create(String fileName, List<String> content) throws WorkspaceException;

    void remove(String fileName) throws WorkspaceException;

    void change(String fileName, List<String> content) throws WorkspaceException;
}
