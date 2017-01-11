# ExcelMetaForce
This is to manipulate salesforce metadata via Excel

This is developing now.

## using
+ poi

## salesforce connection setting
create yaml file ./sfdc_connection_info.yml.  
like this

```yaml
!!jp.co.tv.excelmetaforce.sfdc.SfdcConnectionInfo
username: xxx@xxx
password: xxxx
securityToken: 
partnerUri: https://login.salesforce.com/services/Soap/u/38.0
proxyHost: 
proxyPort: 
proxyUser: 
proxyPass: 
```

## commands
### read from salesforce to excel
```bash
# read custom object (need write object api to general sheet(sheet(0)))
# need delete rows, from row8 to all at field sheet(sheet(3)), from row7 to all at picklist sheet(sheet(4))
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.ReadCustomObject format.xlsx

# read object crud (need write profile to crud sheet(sheet(5)))
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.ReadObjectCrud format.xlsx

# read field permission (need write profile to filed permission sheet(sheet(6)))
# need delete rows, from row8 to all at field permission sheet(sheet(6))
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.ReadFieldPermission format.xlsx
```

### create to salesforce from excel
```bash
# create custom object
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.CreateCustomObject target.xlsx

# create custom fields (put some character at target row, column A)
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.CreateCustomField target.xlsx
```

### update to salesforce from excel
```bash
# update custom object
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.UpdateCustomObject target.xlsx

# update custom fields (put some character at target row, column A)
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.UpdateCustomField target.xlsx

# update object crud (put some character at target row, column A)
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.UpdateObjectCrud target.xlsx

# update field permission (put some character at target row, column A)
java -cp ExcelMetaForce.jar jp.co.tv.excelmetaforce.UpdateFieldPermission target.xlsx
```

## future
+ 差分だけ読めるとよい