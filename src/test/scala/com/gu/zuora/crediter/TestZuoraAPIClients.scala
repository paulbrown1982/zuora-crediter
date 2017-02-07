package com.gu.zuora.crediter

import com.gu.zuora.crediter.Types.{RawCSVText, ZuoraSoapClientError, SerialisedJson}
import com.gu.zuora.soap.{CreateResponse, Error, SaveResult, ZObjectable}

class TestSoapClient extends ZuoraSoapClient {
  override def create(zObjects: Seq[ZObjectable]): Either[ZuoraSoapClientError, CreateResponse] = ???
}

object TestSoapClient {

  def getSuccessfulCreateResponse(source: Seq[Any]) = new ZuoraSoapClient {
    override def create(zObjects: Seq[ZObjectable]): Either[ZuoraSoapClientError, CreateResponse] = {
      assert(zObjects.size == source.size)
      Right(CreateResponse(
        result = zObjects.map(obj => SaveResult(Id = obj.Id))
      ))
    }
  }

  def getUnsuccessfulCreateResponseForHeadSource(source: Seq[Any]) = new ZuoraSoapClient {
    override def create(zObjects: Seq[ZObjectable]): Either[ZuoraSoapClientError, CreateResponse] = {
      assert(zObjects.size == source.size)
      Right(CreateResponse(
        result = zObjects.headOption.map(adjustment => SaveResult(
          Errors = Seq(Some(Error(Message = Some(Some("getUnsuccessfulCreateResponseForHeadSource error")))))
        )).toSeq ++ zObjects.tail.map(adjustment => SaveResult(Id = adjustment.Id))
      ))
    }
  }
}

class TestRestClient extends ZuoraRestClient {
  override def makeRestGET(path: String): SerialisedJson = ???
  override def downloadFile(path: String): RawCSVText = ???
  override def makeRestPOST(path: String)(commandJSON: SerialisedJson): SerialisedJson = ???
}

class TestZuoraAPIClients extends ZuoraAPIClients{
  override val zuoraSoapClient: ZuoraSoapClient = new TestSoapClient
  override val zuoraRestClient: ZuoraRestClient = new TestRestClient
}