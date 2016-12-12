package jp.co.tv.excelmetaforce.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sforce.soap.metadata.DeleteConstraint;
import com.sforce.soap.metadata.DeploymentStatus;
import com.sforce.soap.metadata.EncryptedFieldMaskChar;
import com.sforce.soap.metadata.EncryptedFieldMaskType;
import com.sforce.soap.metadata.FieldType;
import com.sforce.soap.metadata.SharingModel;

public class MetadataToExcelTest {
    private MetadataToExcel converter;

    /**
     * Create excel data
     */
    @Before
    public void setup() {
        converter = new MetadataToExcel();
    }

    @Test
    public void testConvertSharingModel() {
        assertThat(converter.convertSharingModel(SharingModel.Private), is("非公開"));
        assertThat(converter.convertSharingModel(SharingModel.ReadWrite), is("公開（更新可能）"));
        assertThat(converter.convertSharingModel(SharingModel.Read), is("公開（参照のみ）"));
        assertThat(converter.convertSharingModel(SharingModel.ControlledByParent), is("親レコードに連動"));
        assertThat(converter.convertSharingModel(SharingModel.FullAccess), is(""));
    }

    @Test
    public void testConvertDeployment() {
        assertThat(converter.convertDeployment(DeploymentStatus.Deployed), is("○"));
        assertThat(converter.convertDeployment(DeploymentStatus.InDevelopment), is(""));
    }

    @Test
    public void testConvertFieldType() {
        assertThat(converter.convertType(FieldType.Text), is("テキスト"));
        assertThat(converter.convertType(FieldType.AutoNumber), is("自動採番"));
        assertThat(converter.convertType(FieldType.Number), is("数値"));
        assertThat(converter.convertType(FieldType.Date), is("日付"));
        assertThat(converter.convertType(FieldType.DateTime), is("日付／時間"));
        assertThat(converter.convertType(FieldType.Picklist), is("選択リスト"));
        assertThat(converter.convertType(FieldType.Lookup), is("参照関係"));
        assertThat(converter.convertType(FieldType.MasterDetail), is("主従関係"));
        assertThat(converter.convertType(FieldType.Checkbox), is("チェックボックス"));
        assertThat(converter.convertType(FieldType.EncryptedText), is("テキスト（暗号化）"));
        assertThat(converter.convertType(FieldType.TextArea), is("テキストエリア"));
        assertThat(converter.convertType(FieldType.LongTextArea), is("ロングテキストエリア"));
        assertThat(converter.convertType(FieldType.Percent), is("パーセント"));
        assertThat(converter.convertType(FieldType.Currency), is("通貨"));
        assertThat(converter.convertType(FieldType.Email), is("メール"));
        assertThat(converter.convertType(FieldType.Phone), is("電話"));
        assertThat(converter.convertType(FieldType.Url), is("URL"));
        assertThat(converter.convertType(FieldType.MultiselectPicklist), is("複数選択リスト"));
    }

    @Test
    public void testEncryptedFieldChar() {
        assertThat(converter.convertMaskChar(EncryptedFieldMaskChar.asterisk), is("*"));
        assertThat(converter.convertMaskChar(EncryptedFieldMaskChar.X), is("X"));
    }

    @Test
    public void testEncryptedFieldType() {
        assertThat(converter.convertMaskType(EncryptedFieldMaskType.all), is("全ての文字をマスク"));
        assertThat(converter.convertMaskType(EncryptedFieldMaskType.lastFour), is("最後の4桁を表示"));
        assertThat(converter.convertMaskType(EncryptedFieldMaskType.creditCard), is("クレジットカード番号"));
        assertThat(converter.convertMaskType(EncryptedFieldMaskType.nino), is("国民保険番号"));
        assertThat(converter.convertMaskType(EncryptedFieldMaskType.ssn), is("社会保障番号"));
        assertThat(converter.convertMaskType(EncryptedFieldMaskType.sin), is("社会保険番号"));
    }

    @Test
    public void testGetUnique() {
        assertThat(converter.getUnique(true, true), is("○"));
        assertThat(converter.getUnique(true, false), is("△"));
        assertThat(converter.getUnique(false, false), is(""));
    }

    @Test
    public void testDeleteConstraint() {
        assertThat(converter.getDeleteConstraint(DeleteConstraint.SetNull), is("○"));
        assertThat(converter.getDeleteConstraint(DeleteConstraint.Restrict), is("×"));
        assertThat(converter.getDeleteConstraint(null), is(""));
    }

    @Test
    public void testIsNumericType() {
        assertThat(converter.isNumericType(FieldType.Text), is(false));
        assertThat(converter.isNumericType(FieldType.Number), is(true));
        assertThat(converter.isNumericType(FieldType.Currency), is(true));
        assertThat(converter.isNumericType(FieldType.Percent), is(true));
    }
}
