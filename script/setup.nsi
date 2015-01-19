;----------------
; setup.nsi
;----------------

; First is default
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Italian.nlf"
LoadLanguageFile "${NSISDIR}\Contrib\Language files\English.nlf"

; The name of the installer
Name "Visual Zoom 2.1"

; The file to write
OutFile "output\VisualZoom_2.1_setup.exe"

; The default installation directory
InstallDir "$PROGRAMFILES\VisualZoom"

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\VisualZoom" "Install_Dir"

;contenuto della licenza da approvare
LicenseData filelicenza.txt
LicenseForceSelection checkbox

;--------------------------------

; Pages

Page license
Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; The stuff to install
Section "VisualZoom (obbligatorio)"

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath "$INSTDIR"
  File "..\VisualZoom.exe"
  File "..\visualzoom.jar"
  
  SetOutPath "$INSTDIR\icons"
  File /r "..\icons\*.*"
  
  SetOutPath "$INSTDIR\jre"
  File /r "..\jre\*.*"
  
  SetOutPath "$INSTDIR\lib"
  File /r "..\lib\*.*"
  
  SetOutPath "$INSTDIR\settings"
  File "..\settings\*.*"
  
  ; Antony: bisogna rimettere a posto questa variabile,
  ; altrimenti i link non puntano alla giusta directory
  SetOutPath "$INSTDIR"
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\VisualZoom "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\VisualZoom" "DisplayName" "VisualZoom"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\VisualZoom" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\VisualZoom" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\VisualZoom" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Collegamenti dal menu avvio"

  CreateDirectory "$SMPROGRAMS\VisualZoom"
  CreateShortCut "$SMPROGRAMS\VisualZoom\VisualZoom.lnk" "$INSTDIR\VisualZoom.exe" "" "$INSTDIR\VisualZoom.exe" 0
  CreateShortCut "$SMPROGRAMS\VisualZoom\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  
SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\VisualZoom"
  DeleteRegKey HKLM SOFTWARE\VisualZoom

  ; Remove directories used
  RMDir /r "$SMPROGRAMS\VisualZoom"
  RMDir /r "$INSTDIR"

SectionEnd
