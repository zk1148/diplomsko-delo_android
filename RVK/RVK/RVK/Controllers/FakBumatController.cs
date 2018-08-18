using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/FakBumat")]
    public class FakBumatController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public FakBumatController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet("{vt}/{stev}/{skl}")]
        public async Task Get(string vt, string stev, string skl)
        {
            System.String filter = " WHERE VT = '" + vt + "' AND SKL = '" + skl + "' AND STEV = '" + stev + "'  ";

            await SqlPipe.Stream(" select '{\"FakBumat\":[,' + STUFF(( "+
                                 "   select " +
                                 "     ',{\"ARTI\":\"' + cast(b.arti as varchar) + '\"' " +
                                 "     + ',\"NAZIV\":\"' + cast(a.naziv as varchar) + '\"' " +
                                 "     + ',\"KOLICINA\":\"' + cast(CAST(B.KOLI AS int) as varchar) + '\"' " +
                                 "     + ',\"NAZIV1\":\"' + cast(e.naziv1 as varchar) + '\"' " +
                                 "     + '}' " +

                                 " FROM FAKBUMAT B " +
                                 " LEFT JOIN SIARTDT A ON A.SIFRA = B.ARTI " +
                                 " LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +

                                 filter +
                                 " ORDER BY ZST DESC " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ", 
             Response.Body, "[]");

        }

        [HttpGet("{vt}/{stev}/{skl}/{arti}")]
        public async Task Get(string vt, string stev, string skl, string arti)
        {
            System.String filter = " WHERE VT = '" + vt + "' AND B.SKL = '" + skl + "' AND STEV = '" + stev + "' " + " AND ARTI = '" + arti + "' ";

            await SqlPipe.Stream(" SELECT '{\"FakBumat\":[,' + STUFF(( "+
                                 " SELECT " +
                                 "     ',{\"SKL\":\"' + cast(B.SKL as varchar) + '\"' " +
                                 "    + ',\"ARTI\":\"' + cast(B.ARTI as varchar) + '\"' " +
                                 "   + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "   + ',\"KOLICINA\":\"' + cast(CAST(B.KOLI AS int) as varchar) + '\"' " +
                                 "   + ',\"NAZIV1\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "   + ',\"ZALOGA\":\"' + cast(CAST(Z.ZALOGA AS int) as varchar) + '\"' " +
                                 "     + '}' " +
                                 " FROM FAKBUMAT B " +
                                 " LEFT JOIN SIARTDT A ON A.SIFRA = B.ARTI " +
                                 " LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +
                                 " LEFT JOIN(SELECT SKL, SIFART, " +
                                 " SUM(CASE WHEN T.VRSTA = 400 THEN - ISNULL(KOLICINA, 0.) ELSE ISNULL(KOLICINA, 0.) END) AS ZALOGA " +
                                 " FROM GIBMAT G " +
                                 " LEFT JOIN SIVRTRANS T ON T.SIFRA = G.VT " +
                                 " GROUP BY SIFART, SKL) AS Z ON Z.SIFART = A.SIFRA " +filter+
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }


        [HttpPost]
        public async Task Post([FromBody] IzdajaFakbumat izdajaFakbumat)
        {
            string skl = izdajaFakbumat.Skl;
            string vt = izdajaFakbumat.Vt;
            string delnalog = izdajaFakbumat.Delnalog;
            string stev = izdajaFakbumat.Stev;
            string arti = izdajaFakbumat.Arti;
            string koli = izdajaFakbumat.Koli;
            string em = izdajaFakbumat.Em;

            var cmd = new SqlCommand(@" INSERT INTO FAKBUMAT(VT, VRDOK, SKL, STEV, ARTI, DELNALOG, KOLI, EM) " +
                                      " SELECT '" + vt + "', '400', '" + skl + "', '" + stev + "', '" + arti + "', '" + delnalog + "', '" + koli + "', '" + em + "' ");

            await SqlCommand.ExecuteNonQuery(cmd);
        }

        [HttpPut("{vt}/{stev}/{skl}/{arti}/{koli}")]
        public async Task Put(string vt, string stev, string skl, string arti, string koli)
        {

            var cmd = new SqlCommand(@"UPDATE FakBumat " +
                                      "SET KOLI = '" + koli + "' " +
                                      "WHERE STEV = '" + stev + "' AND SKL = '" + skl + "' AND VT = '" + vt + "' AND ARTI='" + arti + "'");
            await SqlCommand.ExecuteNonQuery(cmd);
        }

        [HttpDelete("{vt}/{stev}/{skl}/{arti}")]
        public async Task Delete(string vt, string stev, string skl, string arti)
        {
            var cmd = new SqlCommand(@"DELETE FROM FakBumat WHERE STEV = '" + stev + "' AND SKL = '" + skl + "' AND VT = '" + vt + "' AND ARTI='" + arti + "'");
            await SqlCommand.ExecuteNonQuery(cmd);
        }
    }
}
