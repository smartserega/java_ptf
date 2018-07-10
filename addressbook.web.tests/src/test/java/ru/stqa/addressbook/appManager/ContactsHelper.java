package ru.stqa.addressbook.appManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import ru.stqa.addressbook.model.Contacts;
import ru.stqa.addressbook.model.ContactsData;

import java.util.List;

public class ContactsHelper extends HelperBase {

    public ContactsHelper(WebDriver wd) {
        super(wd);
    }

    public void initContactCreation() {
        click(By.xpath("//div[@id='content']/form/input[21]"));
    }

    public void fillContactData(ContactsData contactsData, boolean creation) {
        type(By.name("firstname"), contactsData.getTest_first_name());
        type(By.name("middlename"), contactsData.getTest_middle_name());
        type(By.name("lastname"), contactsData.getTest_last_name());
        type(By.name("nickname"), contactsData.getTest_nickname());
        type(By.name("title"), contactsData.getTest_title());
        type(By.name("company"), contactsData.getTest_compane());
        type(By.name("address"), contactsData.getTest_address());
        type(By.name("mobile"), contactsData.getMobile());
        type(By.name("email"), contactsData.getEmail());

        if (creation) {
            new Select(wd.findElement(By.name("new_group"))).selectByVisibleText(contactsData.getGroup());
        } else {
            Assert.assertFalse(isElementPresent(By.name("new_group")));
        }
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
            String firstName = cells.get(2).getText();
            String lastName = cells.get(1).getText();
            String[] phones = cells.get(5).getText().split("\n");

            int id = Integer.parseInt(element.findElement(By.tagName("input")).getAttribute("id"));

            groupCashe.add(new ContactsData().withId(id).withFirstName(firstName).withHomePhone(phones[0]).
                    withMobile(phones[1]).withWorkPhone(phones[2]));

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
        wd.navigate().back();
        return new ContactsData().withId(contact.getId()).withFirstName(firstname).
                withLastName(lastname).withHomePhone(home).withMobile(mobile).withWorkPhone(work);
    }
}






