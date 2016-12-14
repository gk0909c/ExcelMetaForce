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
        assertThat(converter.convSharingModel(SharingModel.Private), is("非公開"));
        assertThat(converter.convSharingModel(SharingModel.ReadWrite), is("公開（更新可能）"));
        assertThat(converter.convSharingModel(SharingModel.Read), is("公開（参照のみ）"));
        assertThat(converter.convSharingModel(SharingModel.ControlledByParent), is("親レコードに連動"));
    }

    @Test
    public void testConvertDeployment() {
        assertThat(converter.convDeploymentStatus(DeploymentStatus.Deployed), is("リリース済"));
        assertThat(converter.convDeploymentStatus(DeploymentStatus.InDevelopment), is("開発中"));
    }

    @Test
    public void testConvertFieldType() {
        assertThat(converter.convFieldType(FieldType.Text), is("テキスト"));
        assertThat(converter.convFieldType(FieldType.AutoNumber), is("自動採番"));
        assertThat(converter.convFieldType(FieldType.Number), is("数値"));
        assertThat(converter.convFieldType(FieldType.Date), is("日付"));
        assertThat(converter.convFieldType(FieldType.DateTime), is("日付／時間"));
        assertThat(converter.convFieldType(FieldType.Picklist), is("選択リスト"));
        assertThat(converter.convFieldType(FieldType.Lookup), is("参照関係"));
        assertThat(converter.convFieldType(FieldType.MasterDetail), is("主従関係"));
        assertThat(converter.convFieldType(FieldType.Checkbox), is("チェックボックス"));
        assertThat(converter.convFieldType(FieldType.EncryptedText), is("テキスト（暗号化）"));
        assertThat(converter.convFieldType(FieldType.TextArea), is("テキストエリア"));
        assertThat(converter.convFieldType(FieldType.LongTextArea), is("ロングテキストエリア"));
        assertThat(converter.convFieldType(FieldType.Percent), is("パーセント"));
        assertThat(converter.convFieldType(FieldType.Currency), is("通貨"));
        assertThat(converter.convFieldType(FieldType.Email), is("メール"));
        assertThat(converter.convFieldType(FieldType.Phone), is("電話"));
        assertThat(converter.convFieldType(FieldType.Url), is("URL"));
        assertThat(converter.convFieldType(FieldType.MultiselectPicklist), is("複数選択リスト"));
    }

    @Test
    public void testEncryptedFieldChar() {
        assertThat(converter.convMaskChar(EncryptedFieldMaskChar.asterisk), is("*"));
        assertThat(converter.convMaskChar(EncryptedFieldMaskChar.X), is("X"));
        assertThat(converter.convMaskChar(null), is(""));
    }

    @Test
    public void testEncryptedFieldType() {
        assertThat(converter.convMaskType(EncryptedFieldMaskType.all), is("全ての文字をマスク"));
        assertThat(converter.convMaskType(EncryptedFieldMaskType.lastFour), is("最後の4桁を表示"));
        assertThat(converter.convMaskType(EncryptedFieldMaskType.creditCard), is("クレジットカード番号"));
        assertThat(converter.convMaskType(EncryptedFieldMaskType.nino), is("国民保険番号"));
        assertThat(converter.convMaskType(EncryptedFieldMaskType.ssn), is("社会保障番号"));
        assertThat(converter.convMaskType(EncryptedFieldMaskType.sin), is("社会保険番号"));
        assertThat(converter.convMaskType(null), is(""));
    }

    @Test
    public void testGetUnique() {
        assertThat(converter.getUnique(true, true), is("○"));
        assertThat(converter.getUnique(true, false), is("△"));
        assertThat(converter.getUnique(false, false), is(""));
    }

    @Test
    public void testDeleteConstraint() {
        assertThat(converter.convDeleteConstraint(DeleteConstraint.SetNull), is("○"));
        assertThat(converter.convDeleteConstraint(DeleteConstraint.Restrict), is("×"));
        assertThat(converter.convDeleteConstraint(null), is(""));
    }

    @Test
    public void testIsNumericType() {
        assertThat(converter.isNumericType(FieldType.Text), is(false));
        assertThat(converter.isNumericType(FieldType.Number), is(true));
        assertThat(converter.isNumericType(FieldType.Currency), is(true));
        assertThat(converter.isNumericType(FieldType.Percent), is(true));
    }
}
