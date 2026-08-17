
package IFMS.PageRelatedEnums;

import IFMS.InterFaces.JaliFiles;

public enum FilesEnum implements JaliFiles {
    
    
    test(
        "/FrameWorksLib/Jali.js/test.html",
        true,
        FileTypesEnum.html
    ),
    coreJs(
        "/FrameWorksLib/Jali.js/core.js",
        true,
        FileTypesEnum.js
    ),
    tableFormElement(
        "/FrameWorksLib/Jali.js/custom_elements/dataTable.js",
        true,
        FileTypesEnum.js
    ),
    dataCombo(
        "/FrameWorksLib/Jali.js/custom_elements/dataCombo.js",
        true,
        FileTypesEnum.js
    ),
    findObjectBox(
        "/FrameWorksLib/Jali.js/custom_elements/findObjectBox.js",
        true,
        FileTypesEnum.js
    ),
    cssTableFormData(
        "/FrameWorksLib/JaliFrame.css/readDataTable.css",
        true,
        FileTypesEnum.css
    ),
    dataComboCss(
        "/FrameWorksLib/JaliFrame.css/dataCombo.css",
        true,
        FileTypesEnum.css
    ),
    findObjectBoxCss(
        "/FrameWorksLib/JaliFrame.css/FindObjectBox.css",
        true,
        FileTypesEnum.css
    ),
    cssDataForm(
        "/FrameWorksLib/JaliFrame.css/dataForm.css",
        true,
        FileTypesEnum.css
    );
    
    private final String relativeAddress;
    private final boolean loadedByIframe;
    private final FileTypesEnum fileType;//text/html or text/JavaScript for example
    
    FilesEnum(String relativeAddress, boolean loadedByIframe, FileTypesEnum fileType)
    {
        this.relativeAddress = relativeAddress;
        this.loadedByIframe = loadedByIframe;
        this.fileType = fileType;
        
    }
    
    @Override
    public String relativeAddress(){return this.relativeAddress;}
    @Override
    public boolean loadedByIframe(){return this.loadedByIframe;}
    @Override
    public String getFileType(){return this.fileType.getTechnicalType();} 
    
    
}
