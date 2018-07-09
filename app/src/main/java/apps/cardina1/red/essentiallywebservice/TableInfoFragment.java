package apps.cardina1.red.essentiallywebservice;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TableInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TableInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableInfoFragment extends Fragment {
    private static final String FRAGMENT_TAG = "SelectFragment";
    private static final String ARG_TABLE_NAME = "table_name";
    private static final String ARG_TABLE = "table";

    private static String tableName = "";
    private static ResultTable table = null;

    private OnFragmentInteractionListener mListener;

    public TableInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TableInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TableInfoFragment newInstance(String tableName, ResultTable table) {
        Log.d(FRAGMENT_TAG, "newInstance: tableName = " + tableName);
        TableInfoFragment fragment = new TableInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TABLE_NAME, tableName);
        args.putSerializable(ARG_TABLE, table);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableName = getArguments().getString(ARG_TABLE_NAME);
            table = (ResultTable) getArguments().getSerializable(ARG_TABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table_info, container, false);

        TableLayout tableView = view.findViewById(R.id.table_table_info);
        table.setupTableView(tableView, getActivity());

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction(FRAGMENT_TAG, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
