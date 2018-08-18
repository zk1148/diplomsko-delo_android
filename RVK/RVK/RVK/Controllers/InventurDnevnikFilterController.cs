using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;
using System;

namespace RVK.Controllers
{
    [Produces("application/json")]
    public class InventurDnevnikFilterController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public InventurDnevnikFilterController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }


        [Route("api/InventurDnevnikFilter/{tip}/{sifra}/{skl}")]
        public async Task Get(int tip, string sifra, string skl)
        {
            String filter = " WHERE 1=1 ";
            if (tip == 0) {
                filter = filter + " AND isnull(I.SIFART,'')+isnull(A.NAZIV,'') LIKE '%" + sifra + "%' ";
            }
            else if (tip == 1)
            {
                filter = filter + " AND I.SKL = '" + skl + "' ";
            }
            else if (tip == 2)
            {
                filter = filter + " AND isnull(I.SIFART,'')+isnull(A.NAZIV,'') LIKE '%" + sifra + "%' AND I.SKL = '" + skl + "' ";
            }

            await SqlPipe.Stream(" select '{\"InventurDnevnik\":[,' + STUFF(( " +
                                             " SELECT " +
                                             "    ',{\"ZST\":\"' + cast(I.ZST as varchar) + '\"' " +
                                             "    + ',\"SKL\":\"' + cast(I.SKL as varchar) + '\"' " +
                                             "  + ',\"VT\":\"' + cast(I.VT as varchar) + '\"' " +
                                             "  + ',\"STDOK\":\"' + cast(I.STDOK as varchar) + '\"' " +
                                             "  + ',\"SIFART\":\"' + cast(I.SIFART as varchar) + '\"' " +
                                             "  + ',\"DATUM\":\"' + cast(I.DATUM as varchar) + '\"' " +
                                             "  + ',\"INVKOLI\":\"' + cast(I.INVKOLI as varchar) + '\"' " +
                                             "  + ',\"NAZIV\":\"' + cast(A.NAZIV as varchar) + '\"' " +
                                             "  + ',\"NAZIV_SKL\":\"' + cast(S.NAZIV as varchar) + '\"' " +
                                             "  + ',\"NAZIV_EM\":\"' + cast(E.NAZIV1 as varchar) + '\"' " +
                                             "  + ',\"ZALOGA\":\"' + cast(CAST(Z.ZALOGA AS int) as varchar) + '\"' " +
                                             "    + '}' " +
                                             "                           FROM InventurDnevnik I " +
                                             "                           LEFT JOIN SIARTDT A ON A.SIFRA = I.SIFART " +
                                             "                           LEFT JOIN SISKLAD S ON S.SIFRA = I.SKL " +
                                             "                           LEFT JOIN SIENMEDT E ON E.SIFRA = A.EM " +
                                             "                           LEFT JOIN(SELECT SKL, SIFART, " +
                                             "                                     SUM(CASE WHEN T.VRSTA = 400 THEN - ISNULL(KOLICINA, 0.) ELSE ISNULL(KOLICINA, 0.) END) AS ZALOGA  " +
                                             "                                     FROM GIBMAT G " +
                                             "                                     LEFT JOIN SIVRTRANS T ON T.SIFRA = G.VT " +
                                             "                                     GROUP BY SIFART, SKL) AS Z ON Z.SIFART = A.SIFRA AND Z.SKL = I.SKL " +filter+
                                             "                           ORDER BY I.DATUM DESC " +
                                             " for xml path(''), type " +
                                             " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }



    }
}