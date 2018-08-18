using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;
using System;

namespace RVK.Controllers
{
    [Produces("application/json")]
    public class Siartdt_InventurController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public Siartdt_InventurController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }



        [Route("api/Siartdt_Inventur/{sifra}/{skl}")]
        public async Task Get(string sifra, string skl)
        {
            await SqlPipe.Stream(" select '{\"InventurDnevnik\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "    ',{\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "    + ',\"NAZIV_SKL\":\"' + cast(S.NAZIV as varchar) + '\"' " +
                                 "    + ',\"NAZIV_EM\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "    + ',\"ZALOGA\":\"' + cast(CAST(Z.ZALOGA AS int) as varchar) + '\"' " +
                                 "    + '}' " +
                                 "  FROM SIARTDT A" +
                                 "  LEFT JOIN SISKLAD S ON S.SIFRA = '" + skl + "' " +
                                 "  LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +
                                 "  LEFT JOIN(SELECT SKL, SIFART, " +
                                 "            SUM(CASE WHEN T.VRSTA= 400 THEN -ISNULL(KOLICINA, 0.) ELSE ISNULL(KOLICINA,0.) END) AS ZALOGA " +
                                 "            FROM GIBMAT G " +
                                 "            LEFT JOIN SIVRTRANS T ON T.SIFRA = G.VT " +
                                 "            GROUP BY SIFART, SKL) AS Z ON Z.SIFART = A.SIFRA AND Z.SKL = '" + skl + "' " +
                                 "            WHERE A.SIFRA = '" + sifra + "' "+
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }

        
        
    }
}