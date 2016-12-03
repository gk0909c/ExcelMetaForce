# ExcelMetaForce
This is to manipulate Salesforce Metadata via Excel

This is developing now.

## using
+ poi

## salesforce connection setting
create yaml file ./sfdc_connection_info.yml.  
like this

```yaml
!!jp.co.techvan.handleSfdcDD.sfdc.SfdcConnectionInfo
username: xxx@xxx
password: xxxx
securityToken: 
partnerUri: https://login.salesforce.com/services/Soap/u/38.0
proxyHost: 
proxyPort: 
proxyUser: 
proxyPass: 
```



## テンポラリ設計
### やりたい操作
#### Excelの情報をSalesforceに登録
+ カスタムオブジェクト作成・更新
+ カスタムフィールド作成・更新
+ 関連プロファイル（ObjectCrud, FieldPermission）更新

#### Salesforceの情報をExcelに書き出し
+ カスタムオブジェクト
+ カスタムフィールド
+ 関連プロファイル（ObjectCrud, FieldPermission）

## クラス
### Wrapper
+ コマンドから呼ばれるクラスが9
+ 実処理をするのは3（登録・更新・書き出し）

### コンポーネント
#### sfdc
+ Connector

#### Excel
+ 各種シート（概要、オブジェクト、フィールド、選択リスト、CRUD、項目セキュリティ）

#### Convert
+ Sfdcのデータ構造 ⇔ Excelのデータ構造

#### util
+ Sublist
+ Yamlローダ

削除は対象外
