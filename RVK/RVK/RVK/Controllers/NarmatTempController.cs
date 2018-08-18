using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/NarmatTemp")]
    public class NarmatTempController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public NarmatTempController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet]
        public async Task Get()
        {
            await SqlPipe.Stream(" SELECT '{\"Narmat_TEMP\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "     ',{\"ZST\":\"' + cast(ZST as varchar) + '\"' " +
                                 "     + ',\"SIFART\":\"' + cast(SIFART as varchar) + '\"' " +
                                 "     + ',\"PREVZEMAM\":\"' + cast(PREVZEMAM as varchar) + '\"' " +
                                 "     + ',\"NAZIV\":\"' + cast(NAZIV as varchar) + '\"' " +
                                 "     + ',\"NAZIV1\":\"' + cast(NAZIV1 as varchar) + '\"' " +
                                 "     + ',\"NAROCENO\":\"' + cast(CAST(NAROCENO AS varchar) as varchar) + '\"' " +
                                 "     + ',\"PREVZETO\":\"' + cast(CAST(PREVZETO AS varchar) as varchar) + '\"' " +
                                 "     + '}' " +
                                 " FROM NARMAT_TEMP B " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");
        }

  
        [HttpPost("{vrdok}/{stev}")]
        public async Task Post(string vrdok, string stev)
        {

            var cmd = new SqlCommand(@" DELETE FROM NARMAT_TEMP " +
                                      " INSERT INTO NARMAT_TEMP (NAROCENO, SIFART, NAZIV, NAZIV1, PREVZEMAM, PREVZETO) " +
                                      " SELECT NARMATB.KOLICINA AS NAROCENO, NARMATB.IDENT, SIARTDT.NAZIV AS NAZIV_IDENT, SIENMEDT.NAZIV1 AS NAZIV_EM, NARMATB.KOLICINA-isnull(FB.KOLIPREVZ,0) AS PREVZEMAM, isnull(FB.KOLIPREVZ,0) AS PREVZETO " +
                                      " FROM NARMATB " +
                                      " LEFT OUTER JOIN SIARTDT ON SIARTDT.SIFRA = NARMATB.IDENT " +
                                      " LEFT OUTER JOIN SIENMEDT ON SIENMEDT.SIFRA = NARMATB.EM " +
                                      " LEFT OUTER JOIN NARMATN ON NARMATB.VRDOK = NARMATN.VRDOK AND NARMATN.STEV = NARMATB.STEV " +
                                      " LEFT JOIN "+
                                      "  ( " +
                                      "  SELECT NAROC_VRD, NAROC_STEV, ARTI, SUM(ISNULL(KOLI, 0.)) AS KOLIPREVZ " +
                                      " " +
                                      "  FROM FAKBUMAT " +
                                      "  GROUP BY NAROC_VRD, NAROC_STEV, ARTI " +
                                      "  ) FB ON FB.NAROC_VRD = NARMATB.VRDOK AND FB.NAROC_STEV = NARMATB.STEV AND FB.ARTI = NARMATB.IDENT " +
                                      " WHERE NARMATN.VRDOK = " +vrdok+ " AND NARMATN.STEV = " + stev);
            await SqlCommand.ExecuteNonQuery(cmd);
        }

        [HttpPut("{zst}/{prevzemam}")]
        public async Task Put(string zst, string prevzemam)
        {
            var cmd = new SqlCommand(@"UPDATE NARMAT_TEMP " +
                                      "SET PREVZEMAM = '" + prevzemam + "' " +
                                      "WHERE ZST = " + zst);
            await SqlCommand.ExecuteNonQuery(cmd);
        }
    }
}