package ru.stqa.addressbook.appManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import ru.stqa.addressbook.model.Contacts;
import ru.stqa.addressbook.model.ContactsData;
import ru.stqa.addressbook.model.GroupData;
import ru.stqa.addressbook.model.Groups;

import java.sql.*;
import java.util.List;
import java.util.Set;

public class ContactsHelper extends HelperBase {

    public ContactsHelper(WebDriver wd) {
        super(wd);
    }

    public void initContactCreation() {
        click(By.xpath("//div[@id='content']/form/input[21]"));
    }

    public void fillContactData(ContactsData contactsData, boolean creation) {
        fillContactDataWithoutGroup(contactsData);

        if (creation) {
            if (contactsData.getGroups().size() > 0)
                Assert.assertTrue(contactsData.getGroups().size() == 1);
            new Select(wd.findElement(By.name("new_group")))
                    .selectByVisibleText(contactsData.getGroups().iterator().next().getName());
        } else {
            Assert.assertFalse(isElementPresent(By.name("new_group")));
        }
    }

    public void fillContactDataWithoutGroup(ContactsData contactsData) {
        type(By.name("firstname"), contactsData.getTest_first_name());
        type(By.name("middlename"), contactsData.getTest_middle_name());
        type(By.name("lastname"), contactsData.getTest_last_name());
        type(By.name("nickname"), contactsData.getTest_nickname());
        type(By.name("title"), contactsData.getTest_title());
        type(By.name("company"), contactsData.getTest_compane());
        type(By.name("address"), contactsData.getAddress());
        type(By.name("mobile"), contactsData.getMobile());
        type(By.name("home"), contactsData.getHomePhone());
        type(By.name("work"), contactsData.getWorkPhone());
        type(By.name("email"), contactsData.getEmail1());
        type(By.name("email2"), contactsData.getEmail2());
        type(By.name("email3"), contactsData.getEmail3());

    }

    public void initAddNewContact() {
        click(By.linkText("add new"));
    }

    public void deleteContact() {
        click(By.xpath(".//*[@id=\"content\"]/form[2]/div[2]/input"));
        wd.switchTo().alert().accept();
    }

    public void create(ContactsData contact) {
        initAddNewContact();
        fillContactData(contact, true);
        initContactCreation();
        groupCashe = null;
    }

    public void modifyContact(ContactsData contacts) {
        editContactByid(contacts.getId());
        fillContactData(contacts, false);
        submitContactModification();
        groupCashe = null;
        returntoContactPage();
    }


    public int getContactsCount() {
        return wd.findElements(By.name("selected[]")).size();
    }


    public void selectContactById(int id) {
        wd.findElement(By.cssSelector("input[value='" + id + "']")).click();
    }

    private void editContactByid(int id) {
        wd.findElement(By.cssSelector("a[href='edit.php?id=" + id + "']")).click();
    }


    public void submitContactModification() {
        click(By.name("update"));
    }

    public void returntoContactPage() {
        click(By.linkText("home"));
    }

    public void delete(ContactsData contact) {
        selectContactById(contact.getId());
        deleteContact();
        groupCashe = null;
        returntoContactPage();
    }

    private Contacts groupCashe = null;

    public Contacts all() {
        groupCashe = new Contacts();
        List<WebElement> elements = wd.findElements(By.cssSelector("tr[name=\"entry\"]"));
        for (WebElement element : elements) {
            List<WebElement> cells = element.findElements(By.tagName("td"));
            String lastName = cells.get(1).getText();
            String firstName = cells.get(2).getText();
            String address = cells.get(3).getText();
            String allEmail = cells.get(4).getText();
            String allPhones = cells.get(5).getText();
            int id = Integer.parseInt(element.findElement(By.tagName("input")).getAttribute("id"));

            groupCashe.add(new ContactsData().withId(id).withFirstName(firstName).withLastName(lastName).
                    withAllPhones(allPhones).withAllEmails(allEmail).withAddress(address));

        }
        return new Contacts(groupCashe);
    }

    public ContactsData InfoFromEditForm(ContactsData contact) {
        editContactByid(contact.getId());
        String firstname = wd.findElement(By.name("firstname")).getAttribute("value");
        String lastname = wd.findElement(By.name("lastname")).getAttribute("value");
        String home = wd.findElement(By.name("home")).getAttribute("value");
        String mobile = wd.findElement(By.name("mobile")).getAttribute("value");
        String work = wd.findElement(By.name("work")).getAttribute("value");
        String email = wd.findElement(By.name("email")).getAttribute("value");
        String emai2 = wd.findElement(By.name("email2")).getAttribute("value");
        String emai3 = wd.findElement(By.name("email3")).getAttribute("value");
        String address = wd.findElement(By.name("address")).getAttribute("value");

        wd.navigate().back();
        return new ContactsData().withId(contact.getId()).withFirstName(firstname).
                withLastName(lastname).withHomePhone(home).withMobile(mobile).withWorkPhone(work).withEmail1(email).withEmail2(emai2).withEmail3(emai3).withAddress(address);
    }

    public void addContactToUnicGroup(int contactID, int GroupID) {
//*[@id="content"]//*[@value='18']
        selectContactById(contactID);
        wd.findElement(By.name("to_group")).click();
        wd.findElement(By.xpath("//*[@id=\"content\"]//div[4]//*[@value='" + GroupID + "']")).click();
        wd.findElement(By.xpath("//*[@id=\"content\"]/form[2]/div[4]/input")).click();
    }

    public void deleteContacFromGroup(int contactID, int GroupID) {
        wd.findElement(By.xpath(".//*[@id=\"right\"]/select")).click();
        wd.findElement(By.xpath(".//*[@id=\"right\"]/select//*[@value='" + GroupID + "']")).click();
        selectContactById(contactID);
        wd.findElement(By.name("remove")).click();
    }

    public void deleteUnicContact(int unicName) {
        wd.findElement(By.xpath(".//*[contains(text(), '" + unicName + "')]/../td[last()-9]")).click();
        wd.findElement(By.cssSelector("input[value=Delete]")).click();
        wd.switchTo().alert().accept();
    }


    public int randomNumber() {
        return 1 + (int) ( Math.random() * 1500000 );
    }

    public void createContact(ContactsData contact) {
        initAddNewContact();
        fillContactDataWithoutGroup(contact);
        initContactCreation();
    }

    public ContactsData findAnyContact(Set allContacts) {
        ContactsData contact = (ContactsData) allContacts.iterator().next();
        return contact;
    }


}









