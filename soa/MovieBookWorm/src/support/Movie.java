
package support;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for movie complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="movie">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="audienceScore" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="booksList" type="{http://impl.server/}book" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="criticsScore" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="directorsList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="posterUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "movie", propOrder = {
    "audienceScore",
    "booksList",
    "criticsScore",
    "directorsList",
    "posterUrl",
    "title",
    "year"
})
public class Movie {

    protected int audienceScore;
    @XmlElement(nillable = true)
    protected List<Book> booksList;
    protected int criticsScore;
    @XmlElement(nillable = true)
    protected List<String> directorsList;
    protected String posterUrl;
    protected String title;
    protected int year;

    /**
     * Gets the value of the audienceScore property.
     * 
     */
    public int getAudienceScore() {
        return audienceScore;
    }

    /**
     * Sets the value of the audienceScore property.
     * 
     */
    public void setAudienceScore(int value) {
        this.audienceScore = value;
    }

    /**
     * Gets the value of the booksList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the booksList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBooksList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Book }
     * 
     * 
     */
    public List<Book> getBooksList() {
        if (booksList == null) {
            booksList = new ArrayList<Book>();
        }
        return this.booksList;
    }

    /**
     * Gets the value of the criticsScore property.
     * 
     */
    public int getCriticsScore() {
        return criticsScore;
    }

    /**
     * Sets the value of the criticsScore property.
     * 
     */
    public void setCriticsScore(int value) {
        this.criticsScore = value;
    }

    /**
     * Gets the value of the directorsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the directorsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDirectorsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDirectorsList() {
        if (directorsList == null) {
            directorsList = new ArrayList<String>();
        }
        return this.directorsList;
    }

    /**
     * Gets the value of the posterUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Sets the value of the posterUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosterUrl(String value) {
        this.posterUrl = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the year property.
     * 
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     */
    public void setYear(int value) {
        this.year = value;
    }

}
