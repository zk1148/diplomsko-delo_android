using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/InventurGambo")]
    public class InventurGamboController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public InventurGamboController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet]
        public async Task Get()
        {                
            await SqlPipe.Stream(" Declare @XML   xml = (SELECT SKL, VT, STDOK, SIFART, InventurGambo.DATUM, INVKOLI, SIARTDT.NAZIV " +
                                 "                           FROM InventurGambo " +
                                 "                           LEFT JOIN SIARTDT ON SIARTDT.SIFRA = InventurGambo.SIFART " +
                                 "                           ORDER BY InventurGambo.DATUM DESC for XML RAW) " +
                                 " Declare @JSON  varchar(max) = '''' " +
                                 "  " +
                                 " ;with cteEAV as ( " +
                                 "       Select RowNr     = Row_Number() over (Order By (Select NULL)) " +
                                 "             ,Entity    = xRow.value('@*[1]','varchar(100)') " +
                                 "             ,Attribute = xAtt.value('local-name(.)','varchar(100)') " +
                                 "             ,Value     = xAtt.value('.','varchar(max)')  " +
                                 "        From  @XML.nodes('/row') As A(xRow) " +
                                 "        Cross Apply A.xRow.nodes('./@*') As B(xAtt) ) " +
                                 "      ,cteBld as ( " +
                                 "       Select * " +
                                 //boljša rešitev, vendar potrebujemo kljuè
                                 //"             ,NewRow = IIF(Lag(Entity,1)  over (Partition By Entity Order By (Select NULL))=Entity,'',',{') " +
                                 //"             ,EndRow = IIF(Lead(Entity,1) over (Partition By Entity Order By (Select NULL))=Entity,',','}') " +
                                 "               , NewRow = IIF(Attribute <> 'SKL', '', ',{') " +
                                 "               , EndRow = IIF(Attribute <> 'NAZIV', ',', '}') " +
                                 "               , JSON   = Concat('\"',Attribute,'\":','\"',Value,'\"') " +
                                 "        From  cteEAV ) " +
                                 " Select @JSON = @JSON+NewRow+JSON+EndRow " +
                                 "  From  cteBld  " +
                                 "  " +
                                 //" Select '['+Stuff(@JSON,1,1,'')+']' ",
                                 " Select '{\"InventurGambo\": ['+Stuff(@JSON,1,1,'')+'] }' ",
             Response.Body, "[]");

        }

        // GET api/Todo/5
        [HttpGet("{id}")]
        public async Task Get(string id)
        {
            await SqlPipe.Stream(" Declare @XML   xml = (SELECT SKL, VT, STDOK, SIFART, InventurGambo.DATUM, INVKOLI, SIARTDT.NAZIV " +
                                 "                           FROM InventurGambo " +
                                 "                           LEFT JOIN SIARTDT ON SIARTDT.SIFRA = InventurGambo.SIFART " +
                                 "                           WHERE SIFART = '" + id+"' "+
                                 "                           ORDER BY InventurGambo.DATUM DESC for XML RAW) " +
                                 " Declare @JSON  varchar(max) = '''' " +
                                 "  " +
                                 " ;with cteEAV as ( " +
                                 "       Select RowNr     = Row_Number() over (Order By (Select NULL)) " +
                                 "             ,Entity    = xRow.value('@*[1]','varchar(100)') " +
                                 "             ,Attribute = xAtt.value('local-name(.)','varchar(100)') " +
                                 "             ,Value     = xAtt.value('.','varchar(max)')  " +
                                 "        From  @XML.nodes('/row') As A(xRow) " +
                                 "        Cross Apply A.xRow.nodes('./@*') As B(xAtt) ) " +
                                 "      ,cteBld as ( " +
                                 "       Select * " +
                                 //boljša rešitev, vendar potrebujemo kljuè
                                 //"             ,NewRow = IIF(Lag(Entity,1)  over (Partition By Entity Order By (Select NULL))=Entity,'',',{') " +
                                 //"             ,EndRow = IIF(Lead(Entity,1) over (Partition By Entity Order By (Select NULL))=Entity,',','}') " +
                                 "               , NewRow = IIF(Attribute <> 'SKL', '', ',{') " +
                                 "               , EndRow = IIF(Attribute <> 'NAZIV', ',', '}') " +
                                 "               , JSON   = Concat('\"',Attribute,'\":','\"',Value,'\"') " +
                                 "        From  cteEAV ) " +
                                 " Select @JSON = @JSON+NewRow+JSON+EndRow " +
                                 "  From  cteBld  " +
                                 "  " +
                                 //" Select '['+Stuff(@JSON,1,1,'')+']' ",
                                 " Select '{\"InventurGambo\": ['+Stuff(@JSON,1,1,'')+'] }' ",
            Response.Body, "[]");

        }

        [HttpPost]
        public async Task Post([FromBody] InventurnaPostavka inventurnaPostavka)
        {
            string skl = inventurnaPostavka.Skl;
            string vt = inventurnaPostavka.Vt;
            string stdok = inventurnaPostavka.Stdok;
            string sifart = inventurnaPostavka.Sifart;
            string datum = inventurnaPostavka.Datum;
            string invkoli = inventurnaPostavka.Invkoli;

            var cmd = new SqlCommand(@"INSERT INTO InventurGambo (SKL, VT, STDOK, SIFART, DATUM, INVKOLI) " +
                                      "VALUES ('" + skl + "', '" + vt + "', '" + stdok + "', '" + sifart + "', '" + datum + "', '" + invkoli + "') ");
            await SqlCommand.ExecuteNonQuery(cmd);
        }

        
        [HttpPut("{id}")]
        public async Task Put(string id, [FromBody] InventurnaPostavka inventurnaPostavka)
        {
            string skl = inventurnaPostavka.Skl;
            string vt = inventurnaPostavka.Vt;
            string stdok = inventurnaPostavka.Stdok;
            string sifart = inventurnaPostavka.Sifart;
            string datum = inventurnaPostavka.Datum;
            string invkoli = inventurnaPostavka.Invkoli;

            var cmd = new SqlCommand(@"UPDATE InventurGambo " +
                                      "SET SKL = '" + skl + "', " +
                                      "VT = '" + vt + "', " +
                                      "STDOK = '" + stdok + "', " +
                                      "SIFART = '" + sifart + "', " +
                                      "DATUM = '" + datum + "', " +
                                      "INVKOLI = '" + invkoli + "' " +
                                      "WHERE SIFART = @id");
            cmd.Parameters.AddWithValue("id", id);
            await SqlCommand.ExecuteNonQuery(cmd);
        }

        [HttpDelete("{id}")]
        public async Task Delete(string id)
        {
            var cmd = new SqlCommand(@"DELETE FROM InventurGambo WHERE SIFART = @id");
            cmd.Parameters.AddWithValue("id", id);
            await SqlCommand.ExecuteNonQuery(cmd);
        }
    }
}
