package de.privacy_avare.repository;

import java.util.List;

/**
 * Interface definiert Methoden zum Datenbankzugriff. 
 * Die Methoden sind grundsätzlich nicht an eine gewisse Art von Datenbank gebunden. 
 * Somit kann die zugrundeliegende Datenbank flexibel gegen Alternativen ausgetauscht werden.
 * Durch die Annotation @N1qlPrimaryIndexed wird automatisch ein Primary Index erzeugt, 
 * welcher für die erweiterten Datenbankabfragen benötigt wird.
 * 
 * @autor Lukas Struppek
 * @version 1.0
 */

import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.repository.CrudRepository;

import de.privacy_avare.domain.Profile;

/**
 * Interface definiert diverse Methoden zur Interaktion zwischen Serverprogramm
 * und zugehöriger Datenbank bereit. Methoden sind mithilfe von Spring Data
 * definiert und somit mit einer Vielzahl an Datenbanklösungen kompatibel. Ein
 * Austausch der Datenbank ist somit ohne Änderungen im Programmcode möglich.
 * Anpassung der Einstellungen in der Datei application.properties sind jedoch
 * notwendig.
 * 
 * @author Lukas Struppek
 * @version 1.0
 */
@N1qlPrimaryIndexed
public interface ProfileRepository extends CrudRepository<Profile, String> {

	/**
	 * Liefert alle Profile aus der Datenbank, absteigend nach ProfileId sortiert,
	 * zurück.
	 * 
	 * @return Liste aller vorhanden Profile.
	 */
	List<Profile> findAllByOrderByIdAsc();

	/**
	 * Liefert alle Profile aus der Datenbank, bei welchen die Eigenschaft unSync
	 * den Wert 'true' aufweist.
	 * 
	 * @return Liste aller Profile mit unSync = 'true'
	 */
	List<Profile> findAllByUnSyncTrue();

	/**
	 * Liefert alle Profile aus der Datenbank, bei welchen die Eigenschaft unSync
	 * den Wert 'false' aufweist.
	 * 
	 * @return Liste aller Profile mit unSync = 'false'
	 */
	List<Profile> findAllByUnSyncFalse();

}
