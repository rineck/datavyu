;This file will be executed next to the application bundle image
;I.e. current directory will contain folder datavyu with application files
[Setup]
AppId={{org.datavyu}}
AppName=datavyu
AppVersion=1.5.2-RC1
AppVerName=Datavyu 1.5.2-RC1
AppPublisher=Datavyu Foundation
AppComments=datavyu
AppCopyright=Copyright (C) 2021
;AppPublisherURL=http://www.datavyu.org/
;AppSupportURL=http://www.datavyu.org/contact.html
;AppUpdatesURL=http://www.datavyu.org/download.html
DefaultDirName={code:DefDirRoot}\datavyu
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Datavyu Foundation
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1
OutputBaseFilename=datavyu
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=datavyu\datavyu.ico
UninstallDisplayIcon={app}\datavyu.ico
UninstallDisplayName=datavyu
WizardImageStretch=No
WizardSmallImageFile=datavyu-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=
UsePreviousAppDir=Yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "datavyu\datavyu.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "datavyu\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\datavyu"; Filename: "{app}\datavyu.exe"; IconFilename: "{app}\datavyu.ico"; Check: returnTrue()
Name: "{group}\Uninstall Datavyu"; Filename: "{uninstallexe}"
Name: "{userdesktop}\datavyu"; Filename: "{app}\datavyu.exe";  IconFilename: "{app}\datavyu.ico"; Check: returnTrue()

[Run]
Filename: "{app}\datavyu.exe"; Description: "{cm:LaunchProgram,datavyu}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\datavyu.exe"; Parameters: "-install -svcName ""datavyu"" -svcDesc ""datavyu"" -mainExe ""datavyu.exe""  "; Check: returnFalse()

[UninstallDelete]
Type: filesandordirs; Name: "{code:DefDirRoot}\datavyu"

[UninstallRun]
Filename: "{app}\datavyu.exe "; Parameters: "-uninstall -svcName datavyu -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;

function IsRegularUser(): Boolean;
begin
Result := not (IsAdminLoggedOn or IsPowerUserLoggedOn);
end;

function DefDirRoot(Param: String): String;
begin
if IsRegularUser then
Result := ExpandConstant('{localappdata}')
else
Result := ExpandConstant('{pf}')
end;
