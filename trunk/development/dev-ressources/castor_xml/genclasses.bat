@echo off
rem Lousy.kizura@gmail.com
echo Generate the XML access classes to parse the XML config files
call setcastor.bat
echo use "setcastor.bat"
java org.exolab.castor.builder.SourceGenerator -i wow-manager.xsd -f -package com.gele.tools.wow.wdbearmanager.castor
java org.exolab.castor.builder.SourceGenerator -i wdb-patch.xsd -f -package com.gele.tools.wow.wdbearmanager.castor.patch
echo DONE
