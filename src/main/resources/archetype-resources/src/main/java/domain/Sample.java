#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * Sample class with annotation for persistence 
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Sample {
    /**
     * Technical Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tech_id")
    private long techId;

    /**
     * Sample Title
     */
    @Column(name = "title")
    private String title;

    /**
     * Sample sub title
     */
    @Column(name = "sub_title")
    private String subTitle;


    /**
     * Sample Version
     */
    private int version;
}
