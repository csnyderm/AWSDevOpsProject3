import { Table } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import { useFindOneQuery } from "../../../app/api/taxApi";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function TaxOverviewForm() {

    const { t } = useTranslation();

    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; // temporary
    const { data, isLoading } = useFindOneQuery(userEmail);

    const totalTax = ((data?.totalFedOwed || 0) + (data?.totalStateOwed || 0)).toFixed(2);
    
    return (
        <>
            <Table
                fullWidth
                fixed
                
                bordered={false}
            >
                <thead>
                    <tr className="bg-mint">
                        <th scope="col"><span className="text-white">{t("taxOverview.taxBreakdown")}</span></th>
                        <th scope="col"><span className="text-white">{t("taxOverview.2023taxes")}</span></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <th scope="row">{t("taxOverview.totalIncome")}</th>
                        <td>$ <span className='text-mint'>{data?.totalIncome.toFixed(2)}</span></td>
                    </tr>
                    <tr>
                        <th scope="row">{t("taxOverview.taxableIncome")}</th>
                        <td>$ <span className='text-mint'>{data?.taxableIncome.toFixed(2)}</span></td>
                    </tr>
                    <tr>
                        <th scope="row">{t("taxOverview.totalStateOwed")}</th>
                        <td>$ <span className='text-mint'>{data?.totalStateOwed.toFixed(2)}</span></td>
                    </tr>
                    <tr>
                        <th scope="row">{t("taxOverview.totalFedOwed")}</th>
                        <td>$ <span className='text-mint'>{data?.totalFedOwed.toFixed(2)}</span></td>
                    </tr>
                    <tr>
                        <th scope="row" style={{fontWeight: "bold"}}>{t("taxOverview.totalOwed")}</th>
                        <td>$ <span className='text-mint' style={{fontWeight: "bold"}}>{totalTax}</span></td>
                    </tr>
                    {/* <tr>
                        <th scope="row">{t("taxObligation", {ns: ['main', 'home']})}</th>
                        <td>Hello</td>
                    </tr>
                    <tr>
                        <th scope="row">{t("totalTaxOwed", {ns: ['main', 'home']})}</th>
                        <td>Hello</td>
                    </tr>
                    <tr>
                        <th scope="row">{t("totalTaxReturn", {ns: ['main', 'home']})}</th>
                        <td>Hello</td>
                    </tr> */}
                    </tbody>
            </Table>
        </>
    )
}