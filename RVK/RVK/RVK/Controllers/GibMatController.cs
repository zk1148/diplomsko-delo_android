using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/GibMat")]
    public class GibMatController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public GibMatController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }


        [HttpGet]
        public async Task Get()
        {

            await SqlPipe.Stream(" SELECT '{\"GibMat_TEMP\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "     ',{\"ZST\":\"' + cast(ZST as varchar) + '\"' "+
                                 "     + ',\"SIFART\":\"' + cast(SIFART as varchar) + '\"' " +
                                 "     + ',\"NAZIV\":\"' + cast(NAZIV as varchar) + '\"' " +
                                 "     + ',\"IZDANO\":\"' + cast(IZDANO as varchar) + '\"' " +
                                 "     + ',\"NAZIV1\":\"' + cast(NAZIV1 as varchar) + '\"' " +
                                 "     + ',\"VRACAM\":\"' + cast(CAST(VRACAM AS varchar) as varchar) + '\"' " +
                                 "     + '}' " +
                                 " FROM GIBMAT_TEMP B " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
           Response.Body, "[]");


        }

        [HttpGet("{delNal}")]
        public async Task Get(string delNal)
        {

            await SqlPipe.Stream("if not exists( " +

                                 " SELECT " +
                                 "     ',{\"SIFART\":\"' + cast(SIFART as varchar) + '\"' " +
                                 "     + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "   + ',\"IZDANO\":\"' + cast(CAST(SUM(IZDANO) - ABS(SUM(POVRATNICA)) AS varchar) as varchar) + '\"' " +
                                 "   + ',\"NAZIV1\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "     + '}' " +
                                 " FROM " +
                                 " ( " +
                                 "     SELECT SIFART, (ISNULL(KOLICINA, 0.)) AS IZDANO, 0.0 AS POVRATNICA " +
                                 "     FROM GIBMAT " +
                                 "     WHERE  VT = 133 AND DELNALOG = '" + delNal + "' " +
                                 "     UNION ALL " +
                                 "     SELECT SIFART, 0.0 AS IZDANO, (ISNULL(KOLICINA, 0.)) AS POVRATNICA " +
                                 "     FROM GIBMAT " +
                                 "     WHERE  VT = 138 AND DELNALOG = '" + delNal + "' " +
                                 " ) Q " +
                                 " LEFT JOIN SIARTDT A ON A.SIFRA = Q.SIFART " +
                                 " LEFT JOIN SIENMEDT E ON A.EM = E.SIFRA " +
                                 " GROUP BY SIFART, A.NAZIV, E.NAZIV1 " +
                                 " begin " +
                                 "     select '{\"GibMat\": [] }' " +
                                 " end else  " +
                                 " SELECT '{\"GibMat\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "     ',{\"SIFART\":\"' + cast(SIFART as varchar) + '\"' " +
                                 "     + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                 "   + ',\"IZDANO\":\"' + cast(CAST(SUM(IZDANO) - ABS(SUM(POVRATNICA)) AS varchar) as varchar) + '\"' " +
                                 "   + ',\"NAZIV1\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                 "     + '}' " +
                                 " FROM " +
                                 " ( " +
                                 "     SELECT SIFART, (ISNULL(KOLICINA, 0.)) AS IZDANO, 0.0 AS POVRATNICA " +
                                 "     FROM GIBMAT " +
                                 "     WHERE  VT = 133 AND DELNALOG = '" + delNal + "' AND (ISNULL(KOLICINA, 0.))>0 " +
                                 "     UNION ALL " +
                                 "     SELECT SIFART, 0.0 AS IZDANO, (ISNULL(KOLICINA, 0.)) AS POVRATNICA " +
                                 "     FROM GIBMAT " +
                                 "     WHERE  VT = 138 AND DELNALOG = '" + delNal + "' " +
                                 " ) Q " +
                                 " LEFT JOIN SIARTDT A ON A.SIFRA = Q.SIFART " +
                                 " LEFT JOIN SIENMEDT E ON A.EM = E.SIFRA " +
                                 " GROUP BY SIFART, A.NAZIV, E.NAZIV1 " +
                                 " ORDER BY A.NAZIV " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");
        }


        [HttpPost("{delnalog}")]
        public async Task Post(string delnalog)
        {
            var cmd = new SqlCommand(@" DELETE FROM GIBMAT_TEMP "+
                                      " INSERT INTO GIBMAT_TEMP (SIFART, NAZIV, IZDANO, NAZIV1, VRACAM) "+
                                      " SELECT SIFART, A.NAZIV, CAST(SUM(IZDANO) - ABS(SUM(POVRATNICA)) AS varchar) AS IZDANO, E.NAZIV1, 0 " +
                                      "             FROM " +
                                      "             (  " +
                                      "                 SELECT SIFART, (ISNULL(KOLICINA, 0.)) AS IZDANO, 0.0 AS POVRATNICA " +
                                      "                 FROM GIBMAT  " +
                                      "                 WHERE  VT = 133 AND DELNALOG = '"+delnalog+"' " +
                                      "                 UNION ALL  " +
                                      "                 SELECT SIFART, 0.0 AS IZDANO, (ISNULL(KOLICINA, 0.)) AS POVRATNICA " +
                                      "                 FROM GIBMAT " +
                                      "                 WHERE  VT = 138 AND DELNALOG = '" + delnalog + "' " +
                                      "             ) Q  " +
                                      "             LEFT JOIN SIARTDT A ON A.SIFRA = Q.SIFART  " +
                                      "             LEFT JOIN SIENMEDT E ON A.EM = E.SIFRA  " +
                                      "             GROUP BY SIFART, A.NAZIV, E.NAZIV1  " +
                                      "             ORDER BY A.NAZIV");
            await SqlCommand.ExecuteNonQuery(cmd);
        }

        [HttpPut("{zst}/{vracam}")]
        public async Task Put(string zst, string vracam)
        {
            var cmd = new SqlCommand(@"UPDATE GIBMAT_TEMP " +
                                    "SET VRACAM = '" + vracam + "' " +
                                    "WHERE ZST = "+zst);
            await SqlCommand.ExecuteNonQuery(cmd);
        }
    }
}
