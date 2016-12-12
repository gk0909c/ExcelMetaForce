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
        assertThat(converter.convFieldType("テキスト"), is(FieldType.Text));
        assertThat(converter.convFieldType("自動採番"), is(FieldType.AutoNumber));
        assertThat(converter.convFieldType("数値"), is(FieldType.Number));
        assertThat(converter.convFieldType("日付"), is(FieldType.Date));
        assertThat(converter.convFieldType("日付／時間"), is(FieldType.DateTime));
        assertThat(converter.convFieldType("選択リスト"), is(FieldType.Picklist));
        assertThat(converter.convFieldType("参照関係"), is(FieldType.Lookup));
        assertThat(converter.convFieldType("主従関係"), is(FieldType.MasterDetail));
        assertThat(converter.convFieldType("チェックボックス"), is(FieldType.Checkbox));
        assertThat(converter.convFieldType("テキスト（暗号化）"), is(FieldType.EncryptedText));
        assertThat(converter.convFieldType("テキストエリア"), is(FieldType.TextArea));
        assertThat(converter.convFieldType("ロングテキストエリア"), is(FieldType.LongTextArea));
        assertThat(converter.convFieldType("パーセント"), is(FieldType.Percent));
        assertThat(converter.convFieldType("通貨"), is(FieldType.Currency));
        assertThat(converter.convFieldType("メール"), is(FieldType.Email));
        assertThat(converter.convFieldType("電話"), is(FieldType.Phone));
        assertThat(converter.convFieldType("URL"), is(FieldType.Url));
        assertThat(converter.convFieldType("複数選択リスト"), is(FieldType.MultiselectPicklist));
    }

    @Test
    public void testConvertSharingModel() {
        assertThat(converter.convSharingModel("非公開"), is(SharingModel.Private));
        assertThat(converter.convSharingModel("公開（更新可能）"), is(SharingModel.ReadWrite));
        assertThat(converter.convSharingModel("公開（参照のみ）"), is(SharingModel.Read));
        assertThat(converter.convSharingModel("親レコードに連動"), is(SharingModel.ControlledByParent));
        assertThat(converter.convSharingModel(""), nullValue());
    }

    @Test
    public void testConvertDeployment() {
        assertThat(converter.convDeploymentStatus("リリース済"), is(DeploymentStatus.Deployed));
        assertThat(converter.convDeploymentStatus("開発中"), is(DeploymentStatus.InDevelopment));
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
        assertThat(converter.convDeleteConstraint("○"), is(DeleteConstraint.SetNull));
        assertThat(converter.convDeleteConstraint("×"), is(DeleteConstraint.Restrict));
        assertThat(converter.convDeleteConstraint(""), nullValue());
    }

    @Test
    public void testEncryptedFieldChar() {
        assertThat(converter.convMaskChar("*"), is(EncryptedFieldMaskChar.asterisk));
        assertThat(converter.convMaskChar("X"), is(EncryptedFieldMaskChar.X));
        assertThat(converter.convMaskChar(""), nullValue());
    }

    @Test
    public void testEncryptedFieldType() {
        assertThat(converter.convMaskType("全ての文字をマスク"), is(EncryptedFieldMaskType.all));
        assertThat(converter.convMaskType("最後の4桁を表示"), is(EncryptedFieldMaskType.lastFour));
        assertThat(converter.convMaskType("クレジットカード番号"), is(EncryptedFieldMaskType.creditCard));
        assertThat(converter.convMaskType("国民保険番号"), is(EncryptedFieldMaskType.nino));
        assertThat(converter.convMaskType("社会保障番号"), is(EncryptedFieldMaskType.ssn));
        assertThat(converter.convMaskType("社会保険番号"), is(EncryptedFieldMaskType.sin));
        assertThat(converter.convMaskType(""), nullValue());
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
