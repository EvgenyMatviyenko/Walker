package auth

import akka.util.ByteString
import org.scalatest.FlatSpec
import utils.ByteHelpers._

import scala.util.Random

/**
  * Created by evgenymatviyenko on 11/19/17.
  */
class SRPProtocolSpec extends FlatSpec {
  /*behavior of "calculate"

  it should "correctly calculate bigA and bigM" in {
    val bigN = BigInt("134979947887829697038423723551403023095207887456025318954226841761390682137726077602184445449220427478810803270819774428612438176071371082982164633580183743740276001278573420307589337844356854472579707654468504857049626070910575454537131542326658759051714010395915572009911594052903328214312387997236508254179")
    val g = BigInt("2")
    val bigB = BigInt("79085779000464757477470127938843346531885514836788963604359692054812114508610182631742865401067304288667714089245657110730680148827915499377946104768572253416090594788204664047581789971599280175827974707464039173081981726540408026507218412956749177501593183941389673410155548880375960435504762576179998247521")
    val s = BigInt("2577509697835434688")
    val identity = "person"
    val password = "password1234"
    val (bigA, bigM) = SRPProtocol.calculate(bigN, g, bigB, s, identity, password, new Random(123))
    assert(bigA == BigInt("50616320096479470001329778206347241624100870460350357591821267417488750205024964732340846974840709837476829210344504583589256079425589902101800025125197224362413414808207842298102556961765411281610937141074085578608481273118716680514868826884749868125572376079248936382425923363766036121452225376284730660372"))
    assert(bigM == BigInt("956011102928995942189489376887680238216446846098861147750217776258574253848"))
  }*/

  behavior of "hash"

  it should "correctly encode bytes using SHA-1" in {
    val testData = "test".asBytes
    val expectedData = BigInt("966482230667555116936258103322711973649032657875").asBytes
    assert(SRPProtocol.hash(testData) == expectedData)
  }

  behavior of "token"

  it should "correctly encode identity and password" in {
    val identity = "person"
    val password = "password1234"
    val expectedData = BigInt("24101969294116014979580624962002116752959393397").asBytes
    assert(SRPProtocol.token(identity, password) == expectedData)
  }

  behavior of "hashInterleaved"

  it should "correctly do interleaved encdoing using SHA-1" in {
    val testData = "test".asBytes
    val evenData = BigInt("388270933250405437194678596574182019041649493659").asBytes
    val oddData = BigInt("946178596340344760575518129898275247688755364159").asBytes
    val expectedData = ByteString((evenData zip oddData flatMap { case (x, y) => ByteString(x, y) }).toArray)
    assert(SRPProtocol.hashInterleaved(testData) == expectedData)
  }

  behavior of "calculateBigA"

  it should "take smallA power of smallG modulo bigN" in {
    assert(SRPProtocol.calculateBigA(1234, 4, 100000) == BigInt(35536))
  }

  behavior of "calculateSmallU"

  it should "hash concatenation of bigA bytes and bitB bytes" in {
    val testBigA = 1234
    val testBigB = 2345
    SRPProtocol.calculateSmallU(testBigA, testBigB)

  }
}
