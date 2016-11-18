package jp.co.tv.excelmetaforce.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;

public class ExcelToMetadataTest {
    private ExcelToMetadata converter;

    /**
     * Create excel data
     */
    @Before
    public void setup() {
        converter = new ExcelToMetadata();
    }

    @Test
    public void testConvertFieldType() {
        assertThat(converter.convertType("テキスト"), is(FieldType.Text));
        assertThat(converter.convertType("自動採番"), is(FieldType.AutoNumber));
        assertThat(converter.convertType("数値"), is(FieldType.Number));
        assertThat(converter.convertType("日付"), is(FieldType.Date));
        assertThat(converter.convertType("日付／時間"), is(FieldType.DateTime));
        assertThat(converter.convertType("選択リスト"), is(FieldType.Picklist));
        assertThat(converter.convertType("参照関係"), is(FieldType.Lookup));
        assertThat(converter.convertType("主従関係"), is(FieldType.MasterDetail));
        assertThat(converter.convertType("チェックボックス"), is(FieldType.Checkbox));
        assertThat(converter.convertType("テキスト(暗号化)"), is(FieldType.EncryptedText));
        assertThat(converter.convertType("テキストエリア"), is(FieldType.TextArea));
        assertThat(converter.convertType("ロングテキストエリア"), is(FieldType.LongTextArea));
        assertThat(converter.convertType("パーセント"), is(FieldType.Percent));
        assertThat(converter.convertType("通貨"), is(FieldType.Currency));
        assertThat(converter.convertType("メール"), is(FieldType.Email));
        assertThat(converter.convertType("電話"), is(FieldType.Phone));
        assertThat(converter.convertType("URL"), is(FieldType.Url));
        assertThat(converter.convertType("複数選択リスト"), is(FieldType.MultiselectPicklist));
    }

    @Test
    public void testConvertSharingModel() {
        assertThat(converter.convertSharingModel("非公開"), is(SharingModel.Private));
        assertThat(converter.convertSharingModel("公開（更新可能）"), is(SharingModel.ReadWrite));
        assertThat(converter.convertSharingModel("公開（参照のみ）"), is(SharingModel.Read));
        assertThat(converter.convertSharingModel("親レコードに連動"), is(SharingModel.ControlledByParent));
        assertThat(converter.convertSharingModel(""), is(SharingModel.FullAccess));
    }

    @Test
    public void testConvertDeployment() {
        assertThat(converter.convertDeployment("○"), is(DeploymentStatus.Deployed));
        assertThat(converter.convertDeployment(null), is(DeploymentStatus.InDevelopment));
    }
}
