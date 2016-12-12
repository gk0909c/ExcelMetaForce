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
import com.sforce.soap.metadata.ValueSet;

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
        assertThat(converter.convertType("テキスト（暗号化）"), is(FieldType.EncryptedText));
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

    @Test
    public void testGetUnique() {
        assertThat(converter.getUnique("△"), is(true));
        assertThat(converter.getUnique(""), is(false));
    }

    @Test
    public void testGetCaseSeinsitive() {
        assertThat(converter.getCaseSensitive("○"), is(true));
        assertThat(converter.getCaseSensitive("△"), is(false));
    }

    @Test
    public void testGetGlobalPick() {
        assertThat(converter.getGlobalPick(""), nullValue());
        
        
        ValueSet valueSet = new ValueSet();
        valueSet.setValueSetName("testpick");
        assertThat(converter.getGlobalPick("testpick").getValueSetName(), is("testpick"));
    }

    @Test
    public void testDeleteConstraint() {
        assertThat(converter.deleteConstraint("○"), is(DeleteConstraint.SetNull));
        assertThat(converter.deleteConstraint("×"), is(DeleteConstraint.Restrict));
        assertThat(converter.deleteConstraint(""), nullValue());
    }

    @Test
    public void testEncryptedFieldChar() {
        assertThat(converter.getMaskChar("*"), is(EncryptedFieldMaskChar.asterisk));
        assertThat(converter.getMaskChar("X"), is(EncryptedFieldMaskChar.X));
        assertThat(converter.getMaskChar(""), nullValue());
    }

    @Test
    public void testEncryptedFieldType() {
        assertThat(converter.getMaskType("全ての文字をマスク"), is(EncryptedFieldMaskType.all));
        assertThat(converter.getMaskType("最後の4桁を表示"), is(EncryptedFieldMaskType.lastFour));
        assertThat(converter.getMaskType("クレジットカード番号"), is(EncryptedFieldMaskType.creditCard));
        assertThat(converter.getMaskType("国民保険番号"), is(EncryptedFieldMaskType.nino));
        assertThat(converter.getMaskType("社会保障番号"), is(EncryptedFieldMaskType.ssn));
        assertThat(converter.getMaskType("社会保険番号"), is(EncryptedFieldMaskType.sin));
        assertThat(converter.getMaskType(""), nullValue());
    }

    @Test
    public void testIsNumericType() {
        assertThat(converter.isNumericType(FieldType.Text), is(false));
        assertThat(converter.isNumericType(FieldType.Number), is(true));
        assertThat(converter.isNumericType(FieldType.Currency), is(true));
        assertThat(converter.isNumericType(FieldType.Percent), is(true));
    }

    @Test
    public void testGetVisibleLines() {
        assertThat(converter.getVisibleLines("0"), is(0));
        assertThat(converter.getVisibleLines(""), is(0));
        assertThat(converter.getVisibleLines("2"), is(2));
    }
}
