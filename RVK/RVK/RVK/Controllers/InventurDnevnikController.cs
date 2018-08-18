using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/InventurDnevnik")]
    public class InventurDnevnikController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public InventurDnevnikController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet]
        public async Task Get()
        {
            await SqlPipe.Stream("if not exists( " +
                                 " SELECT " +
                                 "     ',{\"ZST\":\"' + cast(ZST as varchar) + '\"' " +
                                 "     + ',\"SKL\":\"' + cast(SKL as varchar) + '\"' " +
                                 "     + ',\"VT\":\"' + cast(VT as varchar) + '\"' " +
                                 "     + ',\"STDOK\":\"' + cast(STDOK as varchar) + '\"' " +
                                 "     + ',\"SIFART\":\"' + cast(SIFART as varchar) + '\"' " +
                                 "     + ',\"DATUM\":\"' + cast(InventurDnevnik.DATUM as varchar) + '\"' " +
                                 "     + ',\"INVKOLI\":\"' + cast(INVKOLI as varchar) + '\"' " +
                                 "     + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "     + ',\"NAZIV_EM\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "     + '}' " +
                                 "                           FROM InventurDnevnik " +
                                 "                           LEFT JOIN SIARTDT A ON A.SIFRA = InventurDnevnik.SIFART " +
                                 "                           LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +
                                 "         ) " +
                                 " begin " +
                                 "     select '{\"InventurDnevnik\": [] }' " +
                                 " end else  " +
                                 " SELECT '{\"InventurDnevnik\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "     ',{\"ZST\":\"' + cast(ZST as varchar) + '\"' " +
                                 "     + ',\"SKL\":\"' + cast(SKL as varchar) + '\"' " +
                                 "   + ',\"VT\":\"' + cast(VT as varchar) + '\"' " +
                                 "   + ',\"STDOK\":\"' + cast(STDOK as varchar) + '\"' " +
                                 "   + ',\"SIFART\":\"' + cast(SIFART as varchar) + '\"' " +
                                 "   + ',\"DATUM\":\"' + cast(InventurDnevnik.DATUM as varchar) + '\"' " +
                                 "   + ',\"INVKOLI\":\"' + cast(INVKOLI as varchar) + '\"' " +
                                 "   + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "   + ',\"NAZIV_EM\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "     + '}' " +
                                 "                           FROM InventurDnevnik " +
                                 "                           LEFT JOIN SIARTDT A ON A.SIFRA = InventurDnevnik.SIFART " +
                                 "                           LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +
                                 "                           ORDER BY InventurDnevnik.ZST DESC " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }

        // GET api/Todo/5
        [HttpGet("{id}")]
        public async Task Get(string id)
        {
            await SqlPipe.Stream(" select '{\"InventurDnevnik\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "    ',{\"ZST\":\"' + cast(I.ZST as varchar) + '\"' " +
                                 "    + ',\"SKL\":\"' + cast(I.SKL as varchar) + '\"' " +
                                 "    + ',\"VT\":\"' + cast(I.VT as varchar) + '\"' " +
                                 "    + ',\"STDOK\":\"' + cast(I.STDOK as varchar) + '\"' " +
                                 "    + ',\"SIFART\":\"' + cast(I.SIFART as varchar) + '\"' " +
                                 "    + ',\"DATUM\":\"' + cast(I.DATUM as varchar) + '\"' " +
                                 "    + ',\"INVKOLI\":\"' + cast(I.INVKOLI as varchar) + '\"' " +
                                 "    + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "    + ',\"NAZIV_SKL\":\"' + cast(S.NAZIV as varchar) + '\"' " +
                                 "    + ',\"NAZIV_EM\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "    + ',\"ZALOGA\":\"' + cast(CAST(Z.ZALOGA AS int) as varchar) + '\"' " +
                                 "    + '}' " +
                                 "                           FROM InventurDnevnik I " +
                                 "                           LEFT JOIN SIARTDT A ON A.SIFRA = I.SIFART " +
                                 "                           LEFT JOIN SISKLAD S ON S.SIFRA = I.SKL " +
                                 "                           LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +
                                 "                           LEFT JOIN(SELECT SKL, SIFART, " +
                                 "                                     SUM(CASE WHEN T.VRSTA = 400 THEN - ISNULL(KOLICINA, 0.) ELSE ISNULL(KOLICINA, 0.) END) AS ZALOGA  " +
                                 "                                     FROM GIBMAT G " +
                                 "                                     LEFT JOIN SIVRTRANS T ON T.SIFRA = G.VT " +
                                 "                                     GROUP BY SIFART, SKL) AS Z ON Z.SIFART = A.SIFRA AND Z.SKL = I.SKL " +
                                 "                           WHERE I.ZST = '" + id + "' " +
                                 "                           ORDER BY I.DATUM DESC " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
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
            string uporabnik = inventurnaPostavka.Uporabnik;

            var cmd = new SqlCommand(@"INSERT INTO InventurDnevnik (SKL, VT, STDOK, SIFART, DATUM, INVKOLI, UPORABNIK) " +
                                    "VALUES ('" + skl + "', '" + vt + "', '" + stdok + "', '" + sifart + "', '" + datum + "', '" + invkoli + "', '"+uporabnik+"') ");
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

            var cmd = new SqlCommand(@"UPDATE InventurDnevnik " +
                                    "SET SKL = '" + skl + "', " +
                                    "VT = '" + vt + "', " +
                                    "STDOK = '" + stdok + "', " +
                                    "SIFART = '" + sifart + "', " +
                                    "DATUM = '" + datum + "', " +
                                    "INVKOLI = '" + invkoli + "' " +
                                    "WHERE ZST = @id");
            cmd.Parameters.AddWithValue("id", id);
            await SqlCommand.ExecuteNonQuery(cmd);
        }

        [HttpDelete("{id}")]
        public async Task Delete(string id)
        {
            var cmd = new SqlCommand(@"DELETE FROM InventurDnevnik WHERE ZST = @id");
            cmd.Parameters.AddWithValue("id", id);
            await SqlCommand.ExecuteNonQuery(cmd);
        }
    }
}
