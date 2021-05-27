import-module SqlPS -DisableNameChecking
[string]$newPwd = 'thisIsSuperSecret1234321'

try{
    gc -Path c:\Servers.txt | %{
 
        $srvName = $_
        $srv = New-Object Microsoft.SqlServer.Management.Smo.Server $srvName
        $srv.Logins | where{$_.Name -eq 'sa'} | %{
            $_.ChangePassword($newPwd);
        }
    }
}
catch{
    $_ | fl -Force
}