package models
import play.api.Play.current
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports._
import com.novus.salat.dao._
import se.radley.plugin.salat._
import models.MongoContext._

/**
 * タスクリポジトリコンポーネント.
 * 
 * @author michio.nakagawa@gmail.com
 */
trait TaskRepositoryComponent {
  val taskRepository: TaskRepository

  /**
   * タスクリポジトリトレイト.
   * 
   * @author michio.nakagawa@gmail.com
   */
  trait TaskRepository {
    /**
     * タスクを全件取得する.
     * 
     * @return タスクのリスト.
     */
    def findAll(): List[Task]

    /**
     * タスクを1件取得する.
     * 
     * @param id オブジェクトID.
     * 
     * return タスク.
     */
    def findOneById(id: ObjectId): Option[Task]

    /**
     * タスクを登録する.
     * 
     * @param task タスク.
     */
    def save(task: Task)

    /**
     * タスクを削除する.
     * 
     * @param id オブジェクトID.
     */
    def removeById(id: ObjectId)
  }

  /**
   * タスクリポジトリ.
   *
   * 本当は
   * class TaskRepository extends SalatDAO[Task, ObjectId](collection = mongoCollection("tasks")) {}
   * なり、
   * class TaskRepository extends ModelCompanion[Task, ObjectId]  {
   *   dao = new SalatDAO[Task, ObjectId](collection = mongoCollection("tasks")) {}
   * }
   * なり、したかったがサービスクラスなどの利用側のユニットテスト時にリポジトリのモックがうまくつくれなくて一旦断念.
   * 
   * @author michio.nakagawa@gmail.com
   */
  class MongoTaskRepositoryImpl extends TaskRepository {
    object TaskDao extends ModelCompanion[Task, ObjectId] {
      val dao = new SalatDAO[Task, ObjectId](collection = mongoCollection("tasks")) {}
    }

    def findAll(): List[Task]= TaskDao.findAll()
      .sort(orderBy = MongoDBObject("termDate" -> 1)).toList
    def findOneById(id: ObjectId) = TaskDao.findOneById(id)
    def save(task: Task) = TaskDao.save(task)
    def removeById(id: ObjectId) = TaskDao.removeById(id)
  }
}
