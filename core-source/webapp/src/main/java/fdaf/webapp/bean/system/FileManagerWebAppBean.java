/*
 * Copyright (c) Heru Himawan Tejo Laksono. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fdaf.webapp.bean.system;

import fdaf.base.FileManagerInterface;
import fdaf.base.UserType;
import fdaf.webapp.base.AbstractBaseWebAppBean;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// UNDER DEVELOPMENT!
@ViewScoped
@Named
public class FileManagerWebAppBean extends AbstractBaseWebAppBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private Controller controller;
    
    @EJB(lookup = "java:global/__EJB_LOOKUP_DIR__/FileManagerUtil")
    private FileManagerInterface fileManagerUtil;
    
    private LinkedHashMap<String, Map<String, Boolean>> nodes = new LinkedHashMap<String, Map<String, Boolean>>();
    
    @Inject
    private FileManagerDirectoryInfoBean directoryInfo;
    
    @Pattern(regexp = "^[a-zA-Z0-9\\.\\-\\_\\(\\)\\[\\]\\{\\}\\$\\@\\~ ]+$", message = "{newDirectoryNameBadFormat}")
    @Size(min = 1, max = 128, message = "{newDirectoryNameLengthOutOfRange}")
    @NotBlank(message = "{newDirectoryNameBlank}")
    private String newDirectoryName;
    
    private String previewFileAddress;
    
    @Pattern(regexp = "^[a-zA-Z0-9\\.\\-\\_\\(\\)\\[\\]\\{\\}\\$\\@\\~ ]+$", message = "{newFileNameBadFormat}")
    @Size(min = 1, max = 128, message = "{newFileNameLengthOutOfRange}")
    @NotBlank(message = "{newFileNameBlank}")
    private String newFileName;
    
    private boolean inPrepareCreateDirectory;
    
    private boolean inPrepareUpload;
    
    private boolean inPrepareRenameFile;
    private boolean inPrepareMoveFile;
    
    private boolean inPrepareRenameDirectory;
    private boolean inPrepareMoveDirectory;
    
    private boolean inPrepareMoveNodes;
    
    private boolean massiveMoveReadyState;
    private boolean prepareMassiveMoveOp;
    
    private String massiveMoveDirectoryDestination;
    private String moveFileDestinationDirectory;
    private String moveDirectoryDestination;
    
    HashMap<String, String[]> nodeNameMap = new HashMap<String, String[]>();
    String[] dummyNodeNames = new String[]{};
    boolean applyDeselectAll;
    boolean applySelectAll;
    boolean selectAllFlag;
    boolean massiveRemovalReadyState;
    boolean prepareMassiveRemovalOp;

    public FileManagerWebAppBean() {
        // NO-OP
    }
    
    protected Controller getController() {
        return controller;
    }
    
    // ======================================================================
    // Directory retrieval methods
    // ======================================================================
    
    public void toHomeDirectory() {
        fileManagerUtil.toHomeDirectory();
        directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
    }
    
    public void toParentDirectory() {
        fileManagerUtil.toParentDirectory();
        directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
    }
    
    public void setCurrentDirectory(String currentDirectory) {
        directoryInfo.setCurrentDirectory(currentDirectory);
    }
    
    public String getCurrentDirectory() {
        return directoryInfo.getCurrentDirectory();
    }
    
    public boolean getInHomeDirectory() {
        return fileManagerUtil.isInHomeDirectory();
    }
    
    // ======================================================================
    // Population of directories & files
    // ======================================================================

    public void populateNodes(ComponentSystemEvent event) throws AbortProcessingException {
        if (!directoryInfo.isBaseDirectoryInitialized()) {
            String baseDirectory = getCommonConfiguration().getFileManagerHomeDirectory()
                + File.separator + getUserSessionManager().getUserName();
            directoryInfo.setCurrentDirectory(baseDirectory);
            directoryInfo.setBaseDirectory(baseDirectory);
            directoryInfo.markBaseDirectoryInitialized();
        }
        fileManagerUtil.changeBaseDirectory(directoryInfo.getBaseDirectory());
        if (directoryInfo.getCurrentDirectory() == null) {
            directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
        }
        fileManagerUtil.setCurrentDirectory(directoryInfo.getCurrentDirectory());
        directoryInfo.setCurrentDirectory(fileManagerUtil.getCurrentDirectory());
        fileManagerUtil.populateNodes();
        nodes = fileManagerUtil.getNodeMap();
    }
    
    public boolean getIsEmptyDirectory() {
        return nodes.isEmpty();
    }

    public LinkedHashMap<String, Map<String, Boolean>> getNodes() {
        return nodes;
    }
    
    // ======================================================================
    // Shared methods to support new directory creation & directory renaming
    // ======================================================================
    
    public void setNewDirectoryName(String newDirectoryName) {
        this.newDirectoryName = newDirectoryName;
    }
    
    public String getNewDirectoryName() {
        return newDirectoryName;
    }
    
    // ======================================================================
    // Directory creation
    // ======================================================================
    
    public void prepareCreateDirectory() {
        inPrepareCreateDirectory = true;
    }
    
    public boolean getInPrepareCreateDirectory() {
        return inPrepareCreateDirectory;
    }
    
    public void createNewDirectory() {
        if (!fileManagerUtil.createNewDirectory(newDirectoryName)) {
            addMessage(SV_ERROR, "newDirectoryCreationError");
            return;
        }
        addMessage(SV_INFO, "newDirectoryCreationSuccess");
        inPrepareCreateDirectory = false;
        newDirectoryName = null;
    }
    
    public void cancelNewDirectoryCreation() {
        inPrepareCreateDirectory = false;
        newDirectoryName = null;
    }
    
    // ======================================================================
    // File upload
    // ======================================================================
    
    public void prepareUpload() {
        inPrepareUpload = true;
    }
    
    public boolean getInPrepareUpload() {
        return inPrepareUpload;
    }
    
    public void upload() {
    }
    
    public void cancelUpload() {
        inPrepareUpload = false;
    }
    
    // ======================================================================
    // Directory rename
    // ======================================================================
    
    public void prepareRenameDirectory() {
        inPrepareRenameDirectory = true;
    }
    
    public boolean getInPrepareRenameDirectory() {
        return inPrepareRenameDirectory;
    }
    
    public void renameDirectory() {
        try {
            if (!fileManagerUtil.renameCurrentDirectory(newDirectoryName)) {
                addMessage(SV_ERROR, "renameDirectoryFailedWarning");
                return;
            }
            setCurrentDirectory(new File(getCurrentDirectory()).getParent() + File.separator + newDirectoryName);
            populateNodes(null);
            addMessage(SV_INFO, "renameDirectorySuccessInfo");
        } catch (Exception e) {
            addMessage(SV_ERROR, "renameDirectoryFailedWarning");
        }
        inPrepareRenameDirectory = false;
    }
    
    public void cancelRenameDirectory() {
        inPrepareRenameDirectory = false;
    }
    
    // ======================================================================
    // Directory move
    // ======================================================================
    
    public void setMoveDirectoryDestination(String moveDirectoryDestination) {
        this.moveDirectoryDestination = moveDirectoryDestination;
    }

    public String getMoveDirectoryDestination() {
        return moveDirectoryDestination;
    }
    
    public void prepareMoveDirectory() {
        inPrepareMoveDirectory = true;
    }
    
    public boolean getInPrepareMoveDirectory() {
        return inPrepareMoveDirectory;
    }
    
    public void moveDirectory() {
        try {
            if (!fileManagerUtil.move(getCurrentDirectory(), moveDirectoryDestination)) {
                addMessage(SV_ERROR, "moveDirectoryFailedWarning");
                return;
            }
            setCurrentDirectory(moveDirectoryDestination + File.separator + (new File(getCurrentDirectory())).getName());
            populateNodes(null);
            addMessage(SV_INFO, "moveDirectorySuccessInfo");
        } catch (Exception e) {
            addMessage(SV_ERROR, "moveDirectoryFailedWarning");
        }
        inPrepareMoveDirectory = false;
    }
    
    public void cancelMoveDirectory() {
        inPrepareMoveDirectory = false;
    }
    
    // ======================================================================
    // File rename
    // ======================================================================

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void prepareRenameFile() {
        inPrepareRenameFile = true;
    }

    public boolean getInPrepareRenameFile() {
        return inPrepareRenameFile;
    }
  
    public void renameFile() {
        String newFileAddress = fileManagerUtil.getCurrentDirectory() + File.separator + newFileName;
        if (!fileManagerUtil.renameFile(previewFileAddress, newFileName)) {
            addMessage(SV_ERROR, "renameFileFailedWarning");
            return;
        }
        previewFileAddress = newFileAddress;
        addMessage(SV_INFO, "renameFileSuccessInfo");
        inPrepareRenameFile = false;
        newFileName = null;
    }

    public void cancelRenameFile() {
        inPrepareRenameFile = false;
        newFileName = null;
    }
    
    // ======================================================================
    // File move
    // ======================================================================
    
    public void setMoveFileDestinationDirectory(String moveFileDestinationDirectory) {
        this.moveFileDestinationDirectory = moveFileDestinationDirectory;
    }

    public String getMoveFileDestinationDirectory() {
        return moveFileDestinationDirectory;
    }
    
    public void prepareMoveFile() {
        inPrepareMoveFile = true;
    }
    
    public boolean getInPrepareMoveFile() {
        return inPrepareMoveFile;
    }
    
    public void cancelMoveFile() {
        inPrepareMoveFile = false;
    }
    
    public void moveFile() {
        try {
            String newFileAddress = moveFileDestinationDirectory + File.separator + (new File(previewFileAddress)).getName();
            if (!fileManagerUtil.move(previewFileAddress, moveFileDestinationDirectory)) {
                addMessage(SV_ERROR, "moveFileFailedWarning");
                return;
            }
            previewFileAddress = newFileAddress;
            addMessage(SV_INFO, "moveFileSuccessInfo");
        } catch (Exception e) {
            addMessage(SV_ERROR, "moveFileFailedWarning");
        }
        inPrepareMoveFile = false;
    }

    // ======================================================================
    // File preview helper
    // ======================================================================
    
    public void setPreviewFileAddress(String previewFileAddress) {
        this.previewFileAddress = previewFileAddress;
    }
    
    public String getPreviewFileAddress() {
        return previewFileAddress;
    }
    
    public boolean getInPreviewFile() {
        return (previewFileAddress != null && !previewFileAddress.trim().isEmpty()
                && !inPrepareCreateDirectory && !inPrepareUpload
                && !massiveRemovalReadyState && !inPrepareRenameDirectory
                && !inPrepareMoveNodes); 
    }
    
    public void closePreview() {
        previewFileAddress = null;
    }
    
    // ======================================================================
    // Massive selection helper
    // ======================================================================
   
    public void resetMassiveSelection(ComponentSystemEvent event) throws AbortProcessingException {
        applyDeselectAll = false;
        applySelectAll = false;
        selectAllFlag = false;
    }

    private String getSelectItemValue() {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        List<UIComponent> children = component.getChildren();
        if (children != null && !children.isEmpty()) {
            return String.valueOf(((UISelectItem) children.get(0)).getItemValue());
        }
        return null;
    }

    public void selectAll(AjaxBehaviorEvent event) throws AbortProcessingException {
        applyDeselectAll = (!selectAllFlag);
        applySelectAll = selectAllFlag;
    }

    private void putNodeNameByMassSelection(String nodeName) {
        if (applySelectAll) {
            nodeNameMap.put(nodeName, new String[]{nodeName});
        }
        if (applyDeselectAll) {
            resetNodeNameMapPair(nodeName);
        }
    }

    private void resetNodeNameMapPair(String nodeName) {
        nodeNameMap.put(nodeName, new String[]{});
    }

    public void setNodeNames(String[] nodeNames) {
        String nodeName = getSelectItemValue();
        if (nodeName != null) {
            if (applySelectAll) {
                nodeNames = new String[]{nodeName};
            }
            if (applyDeselectAll) {
                nodeNames = new String[]{};
            }
            nodeNameMap.put(nodeName, nodeNames);
        }
    }

    public String[] getNodeNames() {
        String nodeName = getSelectItemValue();
        if (nodeName != null) {
            if (!nodeNameMap.containsKey(nodeName)) {
                resetNodeNameMapPair(nodeName);
                putNodeNameByMassSelection(nodeName);
            } else {
                putNodeNameByMassSelection(nodeName);
                return nodeNameMap.get(nodeName);
            }
        }
        return dummyNodeNames;
    }

    public void setSelectAllFlag(boolean selectAllFlag) {
        this.selectAllFlag = selectAllFlag;
    }

    public boolean getSelectAllFlag() {
        return selectAllFlag;
    }
    
    // ======================================================================
    // Directories & files massive move
    // ======================================================================
    
    public SelectItem[] getDirectorySelection() {
        String currentDirectory = directoryInfo.getCurrentDirectory();
        String parentDirectory = (new File(currentDirectory)).getParent();
        if (inPrepareMoveFile) {
            parentDirectory = (new File(previewFileAddress)).getParent();
        }
        List<SelectItem> itemsTemp = new ArrayList<SelectItem>();
        for (String directory : fileManagerUtil.getDirectoryList()) {
            if (inPrepareMoveDirectory && (currentDirectory.equals(directory)
                || parentDirectory.equals(directory)
                || directory.indexOf(currentDirectory) != -1)) {
                continue;
            }
            if (inPrepareMoveFile && parentDirectory.equals(directory)) {
                continue;
            }
            SelectItem selectItem = new SelectItem();
            selectItem.setValue(directory);
            selectItem.setLabel(directory);
            itemsTemp.add(selectItem);
        }
        return itemsTemp.toArray(new SelectItem[]{});
    }
    
    public void setMassiveMoveDirectoryDestination(String massiveMoveDirectoryDestination) {
        this.massiveMoveDirectoryDestination = massiveMoveDirectoryDestination;
    }
    
    public String getMassiveMoveDirectoryDestination() {
        return massiveMoveDirectoryDestination;
    }
    
    public void presetMassiveMoveReadyState() {
        prepareMassiveMoveOp = true;
        for (String nodeName : nodeNameMap.keySet()) {
            String[] nodeNames = nodeNameMap.get(nodeName);
            if (nodeNames != null && nodeNames.length != 0) {
                massiveMoveReadyState = true;
                return;
            }
        }
    }

    public boolean getMassiveMoveReadyState() {
        return massiveMoveReadyState;
    }

    public void clearMassiveMoveReadyState() {
        massiveMoveReadyState = false;
        prepareMassiveMoveOp = false;
    }

    public boolean getPrepareMassiveMoveOp() {
        return prepareMassiveMoveOp;
    }
    
    public void executeMassiveMove(AjaxBehaviorEvent event) throws AbortProcessingException {
        boolean partiallyMoved = false;
        boolean partiallyLocated = false;
        boolean getMoved = false;
        if (!nodeNameMap.isEmpty()) {
            List<String> nodeNameList = new ArrayList<String>();
            for (String nodeName : nodeNameMap.keySet()) {
                String[] nodeNames = nodeNameMap.get(nodeName);
                if (nodeNames != null && nodeNames.length != 0) {
                    try {
                        if (fileManagerUtil.move(nodeName, massiveMoveDirectoryDestination)) {
                            nodeNameList.add(nodeName);
                            getMoved = true;
                        }
                    } catch (Exception e) {
                        indicateServiceError(e);
                        partiallyMoved = true;
                        break;
                    }
                }
            }
            if (!nodeNameList.isEmpty()) {
                for (String nodeName : nodeNameList) {
                    nodeNameMap.remove(nodeName);
                }
            }
            if (!partiallyLocated && !partiallyMoved && getMoved) {
                addMessage(SV_INFO, "massiveMoveInfo");
            }
            if (partiallyLocated && getMoved) {
                addMessage(SV_WARN, "massiveMovePartialLocatedWarn");
            }
            if (partiallyMoved && getMoved) {
                addMessage(SV_WARN, "massiveMovePartialMovedWarn");
            }
            if (!getMoved) {
                addMessage(SV_ERROR, "massiveMoveError");
            }
        }
        clearMassiveMoveReadyState();
    }
    
    public void cancelMassiveMove() {
        clearMassiveMoveReadyState();
        nodeNameMap.clear();
    }
    
    // ======================================================================
    // Directories & files massive removal
    // ======================================================================

    public void presetMassiveRemovalReadyState() {
        prepareMassiveRemovalOp = true;
        for (String nodeName : nodeNameMap.keySet()) {
            String[] nodeNames = nodeNameMap.get(nodeName);
            if (nodeNames != null && nodeNames.length != 0) {
                massiveRemovalReadyState = true;
                return;
            }
        }
    }

    public boolean getMassiveRemovalReadyState() {
        return massiveRemovalReadyState;
    }

    public void clearMassiveRemovalReadyState() {
        massiveRemovalReadyState = false;
        prepareMassiveRemovalOp = false;
    }

    public boolean getPrepareMassiveRemovalOp() {
        return prepareMassiveRemovalOp;
    }
    
    public void executeMassiveRemoval(AjaxBehaviorEvent event) throws AbortProcessingException {
        boolean partiallyRemoved = false;
        boolean partiallyLocated = false;
        boolean getRemoved = false;
        if (!nodeNameMap.isEmpty()) {
            List<String> nodeNameList = new ArrayList<String>();
            for (String nodeName : nodeNameMap.keySet()) {
                String[] nodeNames = nodeNameMap.get(nodeName);
                if (nodeNames != null && nodeNames.length != 0) {
                    try {
                        if (fileManagerUtil.remove(nodeName)) {
                            nodeNameList.add(nodeName);
                            getRemoved = true;
                        }
                    } catch (Exception e) {
                        indicateServiceError(e);
                        partiallyRemoved = true;
                        break;
                    }
                }
            }
            if (!nodeNameList.isEmpty()) {
                for (String nodeName : nodeNameList) {
                    nodeNameMap.remove(nodeName);
                }
            }
            if (!partiallyLocated && !partiallyRemoved && getRemoved) {
                addMessage(SV_INFO, "massiveFilesRemovalInfo");
            }
            if (partiallyLocated && getRemoved) {
                addMessage(SV_WARN, "massiveFilesRemovalPartialLocatedWarn");
            }
            if (partiallyRemoved && getRemoved) {
                addMessage(SV_WARN, "massiveRemovalFilesPartialRemovedWarn");
            }
            if (!getRemoved) {
                addMessage(SV_ERROR, "massiveFilesRemovalError");
            }
        }
        clearMassiveRemovalReadyState();
    }

    public void cancelRemoval() {
        clearMassiveRemovalReadyState();
        nodeNameMap.clear();
    }
}
